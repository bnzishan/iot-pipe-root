package storm.applications.hooks;

import backtype.storm.hooks.BaseTaskHook;
import backtype.storm.hooks.info.BoltExecuteInfo;
import backtype.storm.hooks.info.EmitInfo;
import backtype.storm.task.TopologyContext;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import storm.applications.metrics.MetricsFactory;
import storm.applications.util.config.Configuration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author mayconbordin
 */
public class BoltMeterHookO extends BaseTaskHook {  // injected to AbstractBolt inside prepare() method

    private Configuration config;   // Configuration extends backtype.storm.Config, adding metrics based configs to storm config
    
    private Meter emittedTuples;  // codehale meter : measures the rate of events ove time
    private Meter receivedTuples;
    private Timer executeLatency;
    
    @Override
    public void prepare(Map conf, TopologyContext context) {  // TopologyContext extends backtype.storm.task.WorkerTopologyContext implements IMetricsContext {
        config = Configuration.fromMap(conf);
        
        MetricRegistry registry = MetricsFactory.createRegistry(config);
        
        String componentId = context.getThisComponentId();
        String taskId      = String.valueOf(context.getThisTaskId());

        emittedTuples  = registry.meter(MetricRegistry.name("emitted", componentId, taskId));
        receivedTuples = registry.meter(MetricRegistry.name("received", componentId, taskId));
        executeLatency = registry.timer(MetricRegistry.name("execute-latency", componentId, taskId));
    }

    @Override
    public void boltExecute(BoltExecuteInfo info) {
        receivedTuples.mark();

        if (info.executeLatencyMs != null) {
            executeLatency.update(info.executeLatencyMs, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void emit(EmitInfo info) {
        emittedTuples.mark();
    }
}
