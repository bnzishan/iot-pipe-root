//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.hobbit.core.components;

import com.rabbitmq.client.QueueingConsumer;
import org.apache.commons.io.IOUtils;
import org.hobbit.core.rabbit.*;
import org.hobbit.utils.EnvVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public abstract class AbstractTaskGenerator extends AbstractPlatformConnectorComponent implements GeneratedDataReceivingComponent {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTaskGenerator.class);
	private static final int DEFAULT_MAX_PARALLEL_PROCESSED_MESSAGES = 1;
	private Semaphore startTaskGenMutex;
	private Semaphore terminateMutex;
	private int generatorId;
	private int numberOfGenerators;
	private long nextTaskId;
	private final int maxParallelProcessedMsgs;
	protected DataSender sender2System;
	protected DataSender sender2EvalStore;
	protected DataReceiver dataGenReceiver;
	protected QueueingConsumer consumer;
	protected boolean runFlag;

	public AbstractTaskGenerator() {
		this(1);
	}

	public AbstractTaskGenerator(int maxParallelProcessedMsgs) {
		this.startTaskGenMutex = new Semaphore(0);
		this.terminateMutex = new Semaphore(0);
		this.maxParallelProcessedMsgs = maxParallelProcessedMsgs;
		this.defaultContainerType = "benchmark";
	}

	public void init() throws Exception {
		super.init();
		this.generatorId = EnvVariables.getInt("HOBBIT_GENERATOR_ID", LOGGER);
		this.nextTaskId = (long)this.generatorId;
		this.numberOfGenerators = EnvVariables.getInt("HOBBIT_GENERATOR_COUNT");
		this.sender2System = DataSenderImpl.builder().queue(this.getFactoryForOutgoingDataQueues(), this.generateSessionQueueName("hobbit.taskgen-system")).build();
		this.sender2EvalStore = DataSenderImpl.builder().queue(this.getFactoryForOutgoingDataQueues(), this.generateSessionQueueName("hobbit.taskgen-evalstore")).build();
		this.dataGenReceiver = DataReceiverImpl.builder().dataHandler(new DataHandler() {
			public void handleData(byte[] data) {
				AbstractTaskGenerator.this.receiveGeneratedData(data);
			}
		}).maxParallelProcessedMsgs(this.maxParallelProcessedMsgs).queue(this.getFactoryForIncomingDataQueues(), this.generateSessionQueueName("hobbit.datagen-taskgen")).build();
	}

	public void run() throws Exception {
		this.sendToCmdQueue((byte) 4);
		this.startTaskGenMutex.acquire();
		this.terminateMutex.acquire();
		this.dataGenReceiver.closeWhenFinished();
		this.sender2System.closeWhenFinished();
		this.sender2EvalStore.closeWhenFinished();
	}

	public void receiveGeneratedData(byte[] data) {
		try {
			this.generateTask(data);
		} catch (Exception var3) {
			LOGGER.error("Exception while generating task.", var3);
		}

	}

	protected abstract void generateTask(byte[] var1) throws Exception;

	protected synchronized String getNextTaskId() {
		String taskIdString = Long.toString(this.nextTaskId);
		this.nextTaskId += (long)this.numberOfGenerators;
		return taskIdString;
	}

	public void receiveCommand(byte command, byte[] data) {
		if(command == 8) {
			LOGGER.info("Received signal to start.");
			this.startTaskGenMutex.release();
		} else if(command == 14) {
			LOGGER.info("Received signal to finish.");
			this.terminateMutex.release();
		}

		super.receiveCommand(command, data);
	}

	protected void sendTaskToEvalStorage(String taskIdString, long timestamp, byte[] data) throws IOException {
		this.sender2EvalStore.sendData(RabbitMQUtils.writeByteArrays((byte[])null, new byte[][]{RabbitMQUtils.writeString(taskIdString), data}, RabbitMQUtils.writeLong(timestamp)));
	}

	protected void sendTaskToSystemAdapter(String taskIdString, byte[] data) throws IOException {
		this.sender2System.sendData(RabbitMQUtils.writeByteArrays(new byte[][]{RabbitMQUtils.writeString(taskIdString), data}));
	}

	public int getGeneratorId() {
		return this.generatorId;
	}

	public int getNumberOfGenerators() {
		return this.numberOfGenerators;
	}

	public void close() throws IOException {
		IOUtils.closeQuietly(this.dataGenReceiver);
		IOUtils.closeQuietly(this.sender2EvalStore);
		IOUtils.closeQuietly(this.sender2System);
		super.close();
	}
}
