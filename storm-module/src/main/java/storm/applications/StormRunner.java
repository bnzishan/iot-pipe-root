package storm.applications;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.*; //AlreadyAliveException;
//import backtype.storm.generated.AuthorizationException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.applications.topology.*;
import storm.applications.util.config.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Utility class to run a Storm topology
 *
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class StormRunner {
	private static final Logger LOG = LoggerFactory.getLogger(StormRunner.class);
	private static final String RUN_LOCAL = "local";
	private static final String RUN_REMOTE = "remote";
	private static final String CFG_PATH = "/config/%s.properties";
	// private static final String CFG_PATH = "tasks/storm_tasks/spike_detection/config/%s.properties";
	private static final String NIMBUS_HOST = "nimbus.host";
	private static final String NIMBUS_THRIFT_PORT = "nimbus.thrift.port";

	// hack for storing kafka zookeeper connect string for kafka-storm spout. (write str in Taskgen to bytes is adding extra \ before : );
	private static final String ZOOKEEPER_KAFKA_CONNECT = "sd.kafka.zookeeper.host";
	public String zKKafkaConnectStr = "";


	private AppDriver driver;
	private Config config;
	List<String> parameters = Lists.newArrayList();
	String topology;
	// NIMBUS HOST and THRIFT PORT
	public String nimbusHost;
	public int thriftPort;
	// description = "Mode for running the topology")
	public String mode = "local";
	// description = "The application to be executed", required = true)
	public String application;
	// required = false, description = "The name of the topology")
	public String topologyName;
	// required = false, description = "Path to the configuration file for the application")
	public String configStr;
	// description = "Runtime in seconds for the topology (local mode only)")
	public int runtimeInSeconds = 300;
	// topology jar location
	public String topologyJarLocation;

	public StormRunner() {

		LOG.info("StormRunner....");

		driver = new AppDriver();

		driver.addApp("ads-analytics", AdsAnalyticsTopology.class);
		driver.addApp("bargain-index", BargainIndexTopology.class);
		driver.addApp("click-analytics", ClickAnalyticsTopology.class);
		driver.addApp("fraud-detection", FraudDetectionTopology.class);
		driver.addApp("linear-road", LinearRoadTopology.class);
		driver.addApp("log-processing", LogProcessingTopology.class);
		driver.addApp("machine-outlier", MachineOutlierTopology.class);
		driver.addApp("reinforcement-learner", ReinforcementLearnerTopology.class);
		driver.addApp("sentiment-analysis", SentimentAnalysisTopology.class);
		driver.addApp("spam-filter", SpamFilterTopology.class);
		driver.addApp("spike-detection", SpikeDetectionTopology.class);
		driver.addApp("trending-topics", TrendingTopicsTopology.class);
		driver.addApp("voipstream", VoIPSTREAMTopology.class);
		driver.addApp("word-count", WordCountTopology.class);
		driver.addApp("traffic-monitoring", TrafficMonitoringTopology.class);

	}




/*

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public void setTopologyName(String topologyName) {
		this.topologyName = topologyName;
	}

	public void setConfigStr(String configStr) {
		this.configStr = configStr;
	}

	public void setRuntimeInSeconds(int runtimeInSeconds) {
		this.runtimeInSeconds = runtimeInSeconds;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public void setNimbusThriftPort(String nimbusThriftPort) {
		this.nimbusThriftPort = nimbusThriftPort;
	}

	public void setNimbusHost(String nimbusHost) {
		LOG.info("-------------------- nimbus host {}", nimbusHost);
		this.nimbusHost = nimbusHost;
	}
	*/


	public void run() throws InterruptedException, AlreadyAliveException, InvalidTopologyException {

		// Loads the configuration file set by the user or the default configuration
		// load default configuration
		if (configStr == null) {
			String cfg = String.format(CFG_PATH, application);
			LOG.info("Loaded default configuration file -->  {}", cfg);
			Properties p = null;
			try {
				p = loadProperties(cfg, (configStr == null));
			} catch (IOException e) {
				e.printStackTrace();
			}
			// set nimbus host
			p.setProperty(NIMBUS_HOST, nimbusHost);
			p.setProperty(NIMBUS_THRIFT_PORT, String.valueOf(thriftPort));

			config = Configuration.fromProperties(p);

		} else {
	//		LOG.info(" se #################  task configurations : {}", configStr);

			// hack for kafka
			LOG.info("Loaded default configuration file --> -->  {}", configStr );
			String str = ZOOKEEPER_KAFKA_CONNECT + "=" + zKKafkaConnectStr;
			configStr += "\n" +str + "\n" ;
		//	configStr += "KEY=" + "VALUE" + "\n" ;
				config = Configuration.fromStr(configStr);
				config.put(NIMBUS_HOST, nimbusHost);
				config.put(NIMBUS_THRIFT_PORT, thriftPort);

				LOG.debug(String.valueOf(config.get(NIMBUS_HOST)));
			LOG.debug(String.valueOf(config.get(NIMBUS_THRIFT_PORT)));

			LOG.info("Loaded default configuration file --> -->  {}", configStr );
			}

		LOG.info("application :  {}", application);
		// Get the descriptor for the given application
		AppDriver.AppDescriptor app = driver.getApp(application);
		if (app == null) {
			throw new RuntimeException("The given application name " + application + " is invalid");
		}

		// In case no topology names is given, create one
		if (topologyName == null) {
			topologyName = String.format("%s-%d", application, new Random().nextInt());
		}

		// Get the topology and execute on Storm
		StormTopology stormTopology = app.getTopology(topologyName, config);

		switch (mode) {
			case RUN_LOCAL:
				runTopologyLocally(stormTopology, topologyName, config, runtimeInSeconds);
				break;
			case RUN_REMOTE:
				System.setProperty("storm.jar", topologyJarLocation);
				runTopologyRemotely(stormTopology, topologyName, config);
				break;
			default:
				throw new RuntimeException("Valid running modes are 'local' and 'remote'");
		}
	}

	public static void main(String[] args) throws Exception {
		StormRunner runner = new StormRunner();
		try {
			LOG.info("Inside StormRunner´s main() method ");
			runner.run();
		} catch (AlreadyAliveException | InvalidTopologyException ex) {
			LOG.error("Error in running topology remotely", ex);
		} catch (InterruptedException ex) {
			LOG.error("Error in running topology locally", ex);
		}
	}

	/**
	 * Run the topology locally
	 *
	 * @param topology         The topology to be executed
	 * @param topologyName     The name of the topology
	 * @param conf             The configurations for the execution
	 * @param runtimeInSeconds For how much time the topology will run
	 * @throws InterruptedException
	 */
	public static void runTopologyLocally(StormTopology topology, String topologyName,
										  Config conf, int runtimeInSeconds) throws InterruptedException {
		LOG.info("Starting Storm on local mode to run for {} seconds", runtimeInSeconds);
		LocalCluster cluster = new LocalCluster();

		LOG.info("Topology {} submitted", topologyName);
		cluster.submitTopology(topologyName, conf, topology);
		Thread.sleep((long) runtimeInSeconds * 1000);

		cluster.killTopology(topologyName);
		LOG.info("Topology {} finished", topologyName);

		cluster.shutdown();
		LOG.info("Local Storm cluster was shutdown", topologyName);
	}

	/**
	 * Run the topology remotely
	 *
	 * @param topology     The topology to be executed
	 * @param topologyName The name of the topology
	 * @param conf         The configurations for the execution
	 * @throws AlreadyAliveException
	 * @throws InvalidTopologyException
	 */
	public static void runTopologyRemotely(StormTopology topology, String topologyName,

										   Config conf) throws AlreadyAliveException, InvalidTopologyException {




	//################################################################################################ //
	//#         Benchmarking execution                                                               # //
	//###############################################################################################  //



		LOG.debug("Submiting  task to Cluster {}   ", topologyName);
//		System.setProperty("storm.jar", <path-to-jar>);   //link to exact file location (w/ dependencies)
		StormSubmitter.submitTopology(topologyName, conf, topology);


	}

	public static Properties loadProperties(String filename, boolean classpath) throws IOException {
		Properties properties = new Properties();
		InputStream is;

		if (classpath) {
			is = StormRunner.class.getResourceAsStream(filename);
		} else {
			is = new FileInputStream(filename);
		}

		properties.load(is);
		is.close();

		return properties;
	}
}
