package storm.applications.hooks;

import backtype.storm.hooks.BaseTaskHook;
import backtype.storm.hooks.info.EmitInfo;
import backtype.storm.hooks.info.SpoutAckInfo;
import backtype.storm.task.TopologyContext;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import storm.applications.metrics.MetricsFactory;
import storm.applications.util.config.Configuration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author mayconbordin
 */
public class SpoutMeterHook extends BaseTaskHook {   // injected into AbstractSpout inside open() method
	private Configuration config;
	private Meter emittedTuples;
	private Timer completeLatency;

	@Override
	public void prepare(Map conf, TopologyContext context) {  //  backtype.storm.task.TopologyContext extends WorkerTopologyContext implements IMetricsContext
		// A TopologyContext is given to bolts and spouts in their prepare() and open() methods,respectively.
		// This object provides information about the component’s place within the topology,
		// such as task ids, inputs and outputs, etc. T

		config = Configuration.fromMap(conf);

		MetricRegistry registry = MetricsFactory.createRegistry(config);  // codehale metrics

		String componentId = context.getThisComponentId();
		String taskId = String.valueOf(context.getThisTaskId());

		emittedTuples = registry.meter(MetricRegistry.name("emitted", componentId, taskId));
		completeLatency = registry.timer(MetricRegistry.name("complete-latency", componentId, taskId));
	}

	@Override
	public void emit(EmitInfo info) {
		emittedTuples.mark();
	}

	@Override
	public void spoutAck(SpoutAckInfo info) {
		if (info.completeLatencyMs != null) {
			completeLatency.update(info.completeLatencyMs, TimeUnit.MILLISECONDS);
		}
	}
}
