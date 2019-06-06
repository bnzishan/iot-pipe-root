Background

Apache Storm versions 0.9+ support a built-in metrics framework for collecting and reporting metrics to external systems. Storm ships with a LoggingMetricsConsumer that can log these metrics to file.

In comparison, storm-graphite (this project) provides a GraphiteMetricsConsumer that reports these metrics to a Graphite server instead of writing to a file.

For large Storm clusters emitting a high volume of metrics, we've included a KafkaReporter that reports metrics to a configurable Kafka topic. Applications leveraging a Kafka consumer can then subscribe to this topic and consume the Avro-encoded metric messages using the supplied Avro schema. This can be useful for sending metric data to multiple endpoints, introducing flow control, and ensuring durability.

The KafkaReporter also features integration with Confluent's RESTful Avro schema storage and retrieval service, Schema Registry.

Storm integration

Installation

The jar file storm-graphite-${VERSION}-all.jar -- and only this jar file -- must be made available on Storm's classpath on every node in a Storm cluster.

IMPORTANT: Do not use storm-graphite-${VERSION}.jar instead of the -all jar file because the former does not include transitive dependencies of storm-graphite. If you do use the wrong jar file, you will run into NoClassDefFoundError at run-time.
Option 1: Package and then install the RPM (or DEB) of storm-graphite on each Storm node. Make sure that the $STORM_HOME/lib/ of your Storm installation matches the installation prefix of the storm-graphite RPM/DEB (which is /opt/storm/lib/). If you need to customize the location, modify build.gradle accordingly (search the file for /opt/storm).
Note: The /opt/storm/lib/ location matches the directory layout created by puppet-storm and the associated RPM wirbelsturm-rpm-storm of Wirbelsturm. So if you use any of these tools already, you're good to go.
Option 2: Place the *-all.jar file into $STORM_HOME/lib/ without packaging the jar file into an RPM or DEB.
Option 1 is typically preferred by those users who already have automated deployment setups (think: Puppet, Ansible).

Configuration

Configuring Storm directly

The GraphiteMetricsConsumer can be registered and configured by adding a snippet similar to the following to storm.yaml (see below) and by configuring the destination Graphite server appropriately (not shown here). Note that an individual consumer cannot be configured to report to both Graphite and Kafka. However, two separate consumers may be registered for each reporter.

Reporting Metrics to Graphite

---
### Note: This is Storm's storm.yaml configuration file

# Controls the time interval between metric reports
topology.builtin.metrics.bucket.size.secs: 10
topology.metrics.consumer.register:
  - class: "com.verisign.storm.metrics.GraphiteMetricsConsumer"
    parallelism.hint: 1
    argument:
      metrics.reporter.name: "com.verisign.storm.metrics.reporters.graphite.GraphiteReporter"
      metrics.graphite.host: "graphite.example.com"
      metrics.graphite.port: "2003"
      metrics.graphite.prefix: "storm.test"
      metrics.graphite.min-connect-attempt-interval-secs: "5"
      # Optional arguments can also be supplied to enable UDP
      metrics.graphite.protocol: "udp"