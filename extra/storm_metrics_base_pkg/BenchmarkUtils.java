package storm.applications.metrics.storm_metrics_base_pkg;

import backtype.storm.Config;
import backtype.storm.utils.Utils;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.Map;

public final class BenchmarkUtils {
	private static final Logger LOG = Logger.getLogger(BenchmarkUtils.class);

	private BenchmarkUtils() {
	}

	public static double max(Iterable<Double> iterable) {
		Iterator<Double> iterator = iterable.iterator();
		double max = Double.MIN_VALUE;
		while (iterator.hasNext()) {
			double d = iterator.next();
			if (d > max) {
				max = d;
			}
		}
		return max;
	}

	public static double avg(Iterable<Double> iterable) {
		Iterator<Double> iterator = iterable.iterator();
		double total = 0.0;
		int num = 0;
		while (iterator.hasNext()) {
			total += iterator.next();
			num++;
		}
		return total / num;
	}

	public static void putIfAbsent(Map map, Object key, Object val) {
		if (!map.containsKey(key)) {
			map.put(key, val);
		}
	}

	public static int getInt(Map map, Object key, int def) {
		return Utils.getInt(Utils.get(map, key, def));
	}

	public static boolean ifAckEnabled(Config config) {
		Object ackers = config.get(Config.TOPOLOGY_ACKER_EXECUTORS);
		if (null == ackers) {
			LOG.warn("acker executors are null");
			return false;
		}
		return Utils.getInt(ackers) > 0;
	}}