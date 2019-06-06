//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.hobbit.core.components;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.hobbit.core.rabbit.*;
import org.hobbit.utils.EnvVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

public abstract class AbstractSystemAdapter extends AbstractPlatformConnectorComponent implements GeneratedDataReceivingComponent, TaskReceivingComponent {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSystemAdapter.class);
	private static final int DEFAULT_MAX_PARALLEL_PROCESSED_MESSAGES = 100;
	private Semaphore terminateMutex;
	private Exception cause;
	private Semaphore causeMutex;
	private final int maxParallelProcessedMsgs;
	protected DataReceiver dataGenReceiver;
	protected DataReceiver taskGenReceiver;
	protected DataSender sender2EvalStore;
	protected Model systemParamModel;

	public AbstractSystemAdapter() {
		this(100);
	}

	public AbstractSystemAdapter(int maxParallelProcessedMsgs) {
		this.terminateMutex = new Semaphore(0);
		this.causeMutex = new Semaphore(1);
		this.maxParallelProcessedMsgs = maxParallelProcessedMsgs;
		this.defaultContainerType = "system";
	}

	public void init() throws Exception {
		super.init();
		this.systemParamModel = EnvVariables.getModel("SYSTEM_PARAMETERS_MODEL", () -> {
			return ModelFactory.createDefaultModel();
		}, LOGGER);
		this.dataGenReceiver = DataReceiverImpl.builder().maxParallelProcessedMsgs(this.maxParallelProcessedMsgs).queue(this.incomingDataQueueFactory, this.generateSessionQueueName("hobbit.datagen-system")).dataHandler(new DataHandler() {
			public void handleData(byte[] data) {
				AbstractSystemAdapter.this.receiveGeneratedData(data);
			}
		}).build();
		this.taskGenReceiver = DataReceiverImpl.builder().maxParallelProcessedMsgs(this.maxParallelProcessedMsgs).queue(this.incomingDataQueueFactory, this.generateSessionQueueName("hobbit.taskgen-system")).dataHandler(new DataHandler() {
			public void handleData(byte[] data) {
				ByteBuffer buffer = ByteBuffer.wrap(data);
				String taskId = RabbitMQUtils.readString(buffer);
				byte[] taskData = RabbitMQUtils.readByteArray(buffer);
				AbstractSystemAdapter.this.receiveGeneratedTask(taskId, taskData);
			}
		}).build();
		this.sender2EvalStore = DataSenderImpl.builder().queue(this.getFactoryForOutgoingDataQueues(), this.generateSessionQueueName("hobbit.system-evalstore")).build();
	}

	public void run() throws Exception {
		this.sendToCmdQueue((byte) 1);
		LOGGER.debug(" System adapter is ready.... ");
		this.terminateMutex.acquire();

		try {
			this.causeMutex.acquire();
			if(this.cause != null) {
				throw this.cause;
			}

			this.causeMutex.release();
		} catch (InterruptedException var2) {
			LOGGER.error("Interrupted while waiting to set the termination cause.");
		}

		this.dataGenReceiver.closeWhenFinished();
		this.taskGenReceiver.closeWhenFinished();
	}

	public void receiveCommand(byte command, byte[] data) {
		if(command == 15) {
			this.terminate((Exception)null);
		}

		super.receiveCommand(command, data);
	}

	protected void sendResultToEvalStorage(String taskIdString, byte[] data) throws IOException {
		byte[] taskIdBytes = taskIdString.getBytes(Charsets.UTF_8);
		int capacity = 8 + taskIdBytes.length + data.length;
		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		buffer.putInt(taskIdBytes.length);
		buffer.put(taskIdBytes);
		buffer.putInt(data.length);
		buffer.put(data);
		this.sender2EvalStore.sendData(buffer.array());
	}

	protected synchronized void terminate(Exception cause) {
		if(cause != null) {
			try {
				this.causeMutex.acquire();
				this.cause = cause;
				this.causeMutex.release();
			} catch (InterruptedException var3) {
				LOGGER.error("Interrupted while waiting to set the termination cause.");
			}
		}

		this.terminateMutex.release();
	}

	public void close() throws IOException {
		IOUtils.closeQuietly(this.dataGenReceiver);
		IOUtils.closeQuietly(this.taskGenReceiver);
		this.sender2EvalStore.closeWhenFinished();
		super.close();
	}
}
