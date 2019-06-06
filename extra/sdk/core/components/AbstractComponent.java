//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.hobbit.core.components;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.io.IOUtils;
import org.hobbit.core.rabbit.RabbitQueueFactory;
import org.hobbit.core.rabbit.RabbitQueueFactoryImpl;
import org.hobbit.utils.EnvVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractComponent implements Component {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComponent.class);
	public static final int NUMBER_OF_RETRIES_TO_CONNECT_TO_RABBIT_MQ = 10;
	public static final long START_WAITING_TIME_BEFORE_RETRY = 5000L;
	private String hobbitSessionId;
	protected RabbitQueueFactory outgoingDataQueuefactory = null;
	protected RabbitQueueFactory incomingDataQueueFactory = null;
	protected String rabbitMQHostName;
	protected ConnectionFactory connectionFactory;

	public AbstractComponent() {
	}

	public void init() throws Exception {
		this.hobbitSessionId = EnvVariables.getString("HOBBIT_SESSION_ID", "SYSTEM");
		this.rabbitMQHostName = EnvVariables.getString("HOBBIT_RABBIT_HOST", LOGGER);
		this.connectionFactory = new ConnectionFactory();
		if(this.rabbitMQHostName.contains(":")) {
			String[] splitted = this.rabbitMQHostName.split(":");
			this.connectionFactory.setHost(splitted[0]);
			this.connectionFactory.setPort(Integer.parseInt(splitted[1]));
		} else {
			this.connectionFactory.setHost(this.rabbitMQHostName);
		}

		this.connectionFactory.setAutomaticRecoveryEnabled(true);
		this.connectionFactory.setNetworkRecoveryInterval(10000);

		this.connectionFactory.setConnectionTimeout(30000);

		this.incomingDataQueueFactory = new RabbitQueueFactoryImpl(this.createConnection());
		this.outgoingDataQueuefactory = new RabbitQueueFactoryImpl(this.createConnection());
	}

	protected Connection createConnection() throws Exception {
		Connection connection = null;

		for(int msg = 0; connection == null && msg <= 10; ++msg) {
			try {
				connection = this.connectionFactory.newConnection();
			} catch (Exception var8) {
				if(msg < 10) {
					long waitingTime = 5000L * (long)(msg + 1);
					LOGGER.warn("Couldn\'t connect to RabbitMQ with try #" + msg + ". Next try in " + waitingTime + "ms.", var8);

					try {
						Thread.sleep(waitingTime);
					} catch (Exception var7) {
						LOGGER.warn("Interrupted while waiting before retrying to connect to RabbitMQ.", var7);
					}
				}
			}
		}

		if(connection == null) {
			String var9 = "Couldn\'t connect to RabbitMQ after 5 retries.";
			LOGGER.error(var9);
			throw new Exception(var9);
		} else {
			return connection;
		}
	}

	public void close() throws IOException {
		IOUtils.closeQuietly(this.incomingDataQueueFactory);
		IOUtils.closeQuietly(this.outgoingDataQueuefactory);
	}

	public String getHobbitSessionId() {
		return this.hobbitSessionId;
	}

	public String generateSessionQueueName(String queueName) {
		return queueName + "." + this.hobbitSessionId;
	}
}
