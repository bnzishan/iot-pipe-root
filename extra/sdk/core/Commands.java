//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.hobbit.core;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.util.HashMap;

public final class Commands {
	public static final byte SYSTEM_READY_SIGNAL = 1;
	public static final byte BENCHMARK_READY_SIGNAL = 2;
	public static final byte DATA_GENERATOR_READY_SIGNAL = 3;
	public static final byte TASK_GENERATOR_READY_SIGNAL = 4;
	public static final byte EVAL_STORAGE_READY_SIGNAL = 5;
	public static final byte EVAL_MODULE_READY_SIGNAL = 6;
	public static final byte DATA_GENERATOR_START_SIGNAL = 7;
	public static final byte TASK_GENERATOR_START_SIGNAL = 8;
	public static final byte EVAL_MODULE_FINISHED_SIGNAL = 9;
	public static final byte EVAL_STORAGE_TERMINATE = 10;
	public static final byte BENCHMARK_FINISHED_SIGNAL = 11;
	public static final byte DOCKER_CONTAINER_START = 12;
	public static final byte DOCKER_CONTAINER_STOP = 13;
	public static final byte DATA_GENERATION_FINISHED = 14;
	public static final byte TASK_GENERATION_FINISHED = 15;
	public static final byte DOCKER_CONTAINER_TERMINATED = 16;
	public static final byte START_BENCHMARK_SIGNAL = 17;
	public static final byte REQUEST_SYSTEM_RESOURCES_USAGE = 18;
	private static final ImmutableMap<Byte, String> ID_TO_COMMAND_NAME_MAP = generateMap();






	public static final byte CUSTOM_COMPONENT_READY=(byte) 255;

	//SYSTEM ADAPTER
	public static final byte  SA2ZK_CONFIG_SENT= (byte) 101;
	public static final byte  SA2ZK_HEALTHCHECK_REQUEST= (byte)102;


	//ZOOKEEPER
	public static final byte  ZKMANAGER_READY_SIGNAL = (byte) 124;

	public static final byte  ZK_CLUSTER_READY_SIGNAL = (byte) 125;


	public static final byte ZOOKEEPER_START_SIGNAL=(byte) 126;
	public static final byte ZKMANAGER_FINISHED_SIGNAL=(byte) 127;
	public static final byte ZKMANAGER_TERMINATED_SIGNAL=(byte) 128;


	/**
	 * The signal sent by the benchmarked system to indicate that it
	 * has finished with a phase of bulk loading.
	 */
	public static final byte  BULK_LOADING_DATA_FINISHED = (byte) 150;

	/**
	 * The signal sent by the benchmark controller to indicate that all
	 * data has successfully sent by the data generators
	 */
	public static final byte  BULK_LOAD_DATA_GEN_FINISHED = (byte) 151;


	public static final byte  BULK_LOAD_DATA_GEN_FINISHED_FROM_DATAGEN = (byte) 152;


	// for storm
	public static final byte TOPOLOGY_SENT = (byte) 160;









	private Commands() {
	}

	private static ImmutableMap<Byte, String> generateMap() {
		HashMap mapping = new HashMap();
		Class clazz = Commands.class;
		Field[] fields = clazz.getFields();

		for(int i = 0; i < fields.length; ++i) {
			try {
				byte commandId = fields[i].getByte((Object)null);
				mapping.put(Byte.valueOf(commandId), fields[i].getName());
			} catch (Exception var6) {
				;
			}
		}

		return ImmutableMap.copyOf(mapping);
	}

	public static String toString(byte command) {
		Byte commandObject = new Byte(command);
		return ID_TO_COMMAND_NAME_MAP.containsKey(commandObject)?(String)ID_TO_COMMAND_NAME_MAP.get(commandObject):Byte.toString(command);
	}
}
