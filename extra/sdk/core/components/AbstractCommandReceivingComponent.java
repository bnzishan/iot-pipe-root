//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.hobbit.core.components;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.hobbit.core.data.StartCommandData;
import org.hobbit.core.data.StopCommandData;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.core.rabbit.RabbitQueueFactory;
import org.hobbit.core.rabbit.RabbitQueueFactoryImpl;
import org.hobbit.utils.EnvVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class AbstractCommandReceivingComponent extends AbstractComponent implements CommandReceivingComponent {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommandReceivingComponent.class);
	public static final long DEFAULT_CMD_RESPONSE_TIMEOUT = 300000L;
	private String containerName;
	private String responseQueueName = null;
	private QueueingConsumer responseConsumer = null;
	protected RabbitQueueFactory cmdQueueFactory;
	protected Channel cmdChannel = null;
	protected String defaultContainerType = "";
	private Set<String> acceptedCmdHeaderIds = new HashSet(5);
	private Gson gson = new Gson();
	protected long cmdResponseTimeout = 300000L;

	public AbstractCommandReceivingComponent() {
	}

	public void init() throws Exception {
		super.init();
		this.addCommandHeaderId(this.getHobbitSessionId());
		this.cmdQueueFactory = new RabbitQueueFactoryImpl(this.createConnection());
		this.cmdChannel = this.cmdQueueFactory.getConnection().createChannel();
		String queueName = this.cmdChannel.queueDeclare().getQueue();
		this.cmdChannel.exchangeDeclare("hobbit.command", "fanout", false, true, (Map)null);
		this.cmdChannel.queueBind(queueName, "hobbit.command", "");
		DefaultConsumer consumer = new DefaultConsumer(this.cmdChannel) {
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
				try {
					AbstractCommandReceivingComponent.this.handleCmd(body, properties.getReplyTo());
				} catch (Exception var6) {
					AbstractCommandReceivingComponent.LOGGER.error("Exception while trying to handle incoming command.", var6 );
				}

			}
		};
		this.cmdChannel.basicConsume(queueName, true, consumer);
		this.containerName = EnvVariables.getString("HOBBIT_CONTAINER_NAME", this.containerName);
		if(this.containerName == null) {
			// LOGGER.info("Couldn\'t get the id of this Docker container. Won\'t be able to create containers.");
		}

	}

	protected void sendToCmdQueue(byte command) throws IOException {
		this.sendToCmdQueue(command, (byte[])null);
	}

	protected void sendToCmdQueue(byte command, byte[] data) throws IOException {
		this.sendToCmdQueue(command, data, (BasicProperties)null);
	}

	protected void sendToCmdQueue(byte command, byte[] data, BasicProperties props) throws IOException {
		byte[] sessionIdBytes = this.getHobbitSessionId().getBytes(Charsets.UTF_8);
		int dataLength = sessionIdBytes.length + 5;
		boolean attachData = data != null && data.length > 0;
		if(attachData) {
			dataLength += data.length;
		}

		ByteBuffer buffer = ByteBuffer.allocate(dataLength);
		buffer.putInt(sessionIdBytes.length);
		buffer.put(sessionIdBytes);
		buffer.put(command);
		if(attachData) {
			buffer.put(data);
		}

		this.cmdChannel.basicPublish("hobbit.command", "", props, buffer.array());
	}

	protected void addCommandHeaderId(String sessionId) {
		this.acceptedCmdHeaderIds.add(sessionId);
	}

	protected void handleCmd(byte[] bytes, String replyTo) {

		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		String sessionId = RabbitMQUtils.readString(buffer);

		if(this.acceptedCmdHeaderIds.contains(sessionId)) {

			byte command = buffer.get();

			AbstractCommandReceivingComponent.LOGGER.debug("acrc handleCmd details: sessionId is present : (command={}) ::   " , command  );

			byte[] remainingData;
			if(buffer.remaining() > 0) {
				remainingData = new byte[buffer.remaining()];
				buffer.get(remainingData);
			} else {
				remainingData = new byte[0];
			}

			this.receiveCommand(command, remainingData);
		}

	}

	protected String createContainer(String imageName, String[] envVariables) {
		return this.createContainer(imageName, this.defaultContainerType, envVariables);
	}

	protected String createContainer(String imageName, String containerType, String[] envVariables) {
		try {

			envVariables = envVariables != null?(String[])Arrays.copyOf(envVariables, envVariables.length + 2):new String[2];
			envVariables[envVariables.length - 2] = "HOBBIT_RABBIT_HOST=" + this.rabbitMQHostName;
			envVariables[envVariables.length - 1] = "HOBBIT_SESSION_ID=" + this.getHobbitSessionId();
			this.initResponseQueue();
			byte[] e = RabbitMQUtils.writeString(this.gson.toJson(new StartCommandData(imageName, containerType, this.containerName, envVariables)));
			BasicProperties props = (new Builder()).deliveryMode(Integer.valueOf(2)).replyTo(this.responseQueueName).build();
			this.sendToCmdQueue((byte) 12, e, props);
			LOGGER.debug("ACRC ----------------  command:12 sent for creating container  with  image : " + imageName + " and container name:    " + this.containerName);
			Delivery delivery = this.responseConsumer.nextDelivery(this.cmdResponseTimeout);
			Objects.requireNonNull(delivery, "ACRC ----------  Didn\'t got a response for a create container message.");
			if(delivery.getBody().length > 0) {
				return RabbitMQUtils.readString(delivery.getBody());
			}
		} catch (Exception var7) {
			LOGGER.error("ACRC -----------------  Got exception while trying to request the creation of an instance of the \"" + imageName + "\" image.", var7);
		}

		return null;
	}

	protected void stopContainer(String containerName) {
		byte[] data = RabbitMQUtils.writeString(this.gson.toJson(new StopCommandData(containerName)));

		try {
			this.sendToCmdQueue((byte) 13, data);
		} catch (IOException var4) {
			LOGGER.error("Got exception while trying to stop the container with the id\"" + containerName + "\".", var4);
		}

	}

	private void initResponseQueue() throws IOException {
		if(this.responseQueueName == null) {
			this.responseQueueName = this.cmdChannel.queueDeclare().getQueue();
		}

		if(this.responseConsumer == null) {
			this.responseConsumer = new QueueingConsumer(this.cmdChannel);
			this.cmdChannel.basicConsume(this.responseQueueName, this.responseConsumer);
		}

	}

	public long getCmdResponseTimeout() {
		return this.cmdResponseTimeout;
	}

	public void setCmdResponseTimeout(long cmdResponseTimeout) {
		this.cmdResponseTimeout = cmdResponseTimeout;
	}

	public void close() throws IOException {
		if(this.cmdChannel != null) {
			try {
				this.cmdChannel.close();
			} catch (Exception var2) {
				;
			}
		}

		IOUtils.closeQuietly(this.cmdQueueFactory);
		super.close();
	}
}
