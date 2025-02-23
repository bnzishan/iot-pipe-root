package storm.applications.metrics.storm_metrics_base_pkg;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */


import backtype.storm.Config;
import backtype.storm.generated.StormTopology;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;

import java.util.Set;

public abstract class StormBenchmark implements IBenchmark {

	private static final Logger LOG = Logger.getLogger(StormBenchmark.class);
	public static final String DEFAULT_TOPOLOGY_NAME = "benchmark";

	@Override
	public IMetricsCollector getMetricsCollector(Config config, StormTopology topology) {

		Set<IMetricsCollector.MetricsItem> items = Sets.newHashSet(
				IMetricsCollector.MetricsItem.SUPERVISOR_STATS,
				IMetricsCollector.MetricsItem.TOPOLOGY_STATS,
				IMetricsCollector.MetricsItem.THROUGHPUT,
				IMetricsCollector.MetricsItem.SPOUT_THROUGHPUT,
				IMetricsCollector.MetricsItem.SPOUT_LATENCY
		);
		return new BasicMetricsCollector(config, topology, items);
	}


}