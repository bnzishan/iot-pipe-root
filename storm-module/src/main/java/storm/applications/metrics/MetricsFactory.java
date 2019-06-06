package storm.applications.metrics;

import com.codahale.metrics.*;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.github.aloomaio.storm.metrics.GraphiteMetricsConsumer;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import storm.applications.util.config.Configuration;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static storm.applications.util.config.Configuration.*;

/**
 *
 * @author mayconbordin
 */
public class MetricsFactory {

    private static final Logger LOG = Logger.getLogger(MetricsFactory.class);

    public static final String CONSOLE_REPORTER = "console";
    public static final String CSV_REPORTER     = "csv";
    public static final String SLF4J_REPORTER   = "slf4j";

    // to forward storm metrics to graphite
    public static final String GRAPHITE_REPORTER   = "graphite";


    private static  String GRAPHITE_HOST = "graphite.server";
    private static  int CARBON_AGGREGATOR_LINE_RECEIVER_PORT = 2003; //2023;
    private static  int GRAPHITE_REPORT_INTERVAL_IN_SECONDS = 10;
    private static  String GRAPHITE_METRICS_NAMESPACE_PREFIX = "production.apps.graphitedemo";
    private static Pattern hostnamePattern =
            Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9-]*(\\.([a-zA-Z0-9][a-zA-Z0-9-]*))*$");



    public static MetricRegistry createRegistry(Configuration config) {
        if (!config.getBoolean(METRICS_ENABLED, false))
            return null;
        
        MetricRegistry registry = new MetricRegistry();
        
        String reporterType = config.getString(METRICS_REPORTER, CONSOLE_REPORTER);
        ScheduledReporter reporter;

        // graphite reporter
        GraphiteReporter graphiteReporter;
        reporterType = config.getString(METRICS_REPORTER, GRAPHITE_REPORTER); // overriding console reporter
        final Graphite graphite = new Graphite(new InetSocketAddress(GRAPHITE_HOST,
                CARBON_AGGREGATOR_LINE_RECEIVER_PORT));  // should be read from storm.yaml

        Map<String, String> env = System.getenv();
        GRAPHITE_HOST = env.get("GRAPHITE_HOST");
        CARBON_AGGREGATOR_LINE_RECEIVER_PORT = Integer.parseInt(env.get("GRAPHITE_PORT"));

        // conf for metrics can be set directly in storm.yaml or programmatically as follows
        // commented in storm.yaml at container creation
        config.setDebug(false);
        config.put("topology.builtin.metrics.bucket.size.secs", 1);
        config.registerMetricsConsumer(GraphiteMetricsConsumer.class, 1);  // 4);
        config.put("metrics.reporter.name", "com.verisign.storm.metrics.reporters.graphite.GraphiteReporter");
        config.put("metrics.graphite.host", GRAPHITE_HOST);
        config.put("metrics.graphite.port", CARBON_AGGREGATOR_LINE_RECEIVER_PORT);
        config.put("metrics.graphite.prefix", "production.apps.graphitedemo");



        switch (reporterType) {
            /**
             * A reporter class for logging metrics values to a SLF4J {@link Logger} periodically, similar to
             * {@link ConsoleReporter} or {@link CsvReporter}, but using the SLF4J framework instead. It also
             * supports specifying a {@link Marker} instance that can be used by custom appenders and filters
             * for the bound logging toolkit to further process metrics reports.
             */
            case SLF4J_REPORTER:

                reporter = Slf4jReporter.forRegistry(registry)
                        .outputTo(LoggerFactory.getLogger("storm.applications.metrics"))
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .build();
                break;

            /**
             * codehale: A reporter which creates a comma-separated values file of the measurements for each metric.
             */
            case CSV_REPORTER:
                //
                String outDir = config.getString(METRICS_OUTPUT, "/tmp");
                reporter = CsvReporter.forRegistry(registry)
                        .formatFor(Locale.US)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .build(new File(outDir));
                break;

                // added support for graphite reporter
            case GRAPHITE_REPORTER:

                reporter = GraphiteReporter.forRegistry(registry)
                        .prefixedWith(metricsPath())
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .filter(MetricFilter.ALL)
                        .build(graphite);
                break;

            /**
             * console reporter: codehale: A reporter which outputs measurements to a {@link PrintStream}, like {@code System.out}.
             */

            default:

                reporter = ConsoleReporter.forRegistry(registry)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .build();
                break;

        }
        
        int interval = config.getInt(METRICS_INTERVAL_VALUE, 5);
        TimeUnit unit = TimeUnit.valueOf(config.getString(METRICS_INTERVAL_UNIT, "SECONDS").toUpperCase());
        
        reporter.start(interval, unit);
        return registry;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------


    private static String metricsPath() {
        //  final String myHostname = extractHostnameFromFQHN(detectHostname());
        final String myHostname = detectHostname(); // docker container name ?
        return GRAPHITE_METRICS_NAMESPACE_PREFIX + "." + myHostname;
    }
    private static String detectHostname() {
        String hostname = "hostname-could-not-be-detected";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            LOG.error("Could not determine hostname");
        }
        return hostname;
    }

    private static String extractHostnameFromFQHN(String fqhn) {
        if (hostnamePattern.matcher(fqhn).matches()) {
            if (fqhn.contains(".")) {
                return fqhn.split("\\.")[0];
            }
            else {
                return fqhn;
            }
        }
        else {
            // We want to return the input as-is
            // when it is not a valid hostname/FQHN.
            return fqhn;
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------

}
