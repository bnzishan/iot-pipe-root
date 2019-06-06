package org.hobbit.sdk.utils;

import com.rabbitmq.client.*;
import org.hobbit.core.Commands;
import org.hobbit.core.Constants;
import org.hobbit.core.components.AbstractCommandReceivingComponent;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.core.rabbit.RabbitQueueFactoryImpl;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Roman Katerinenko
 */
public class CommandSender extends AbstractCommandReceivingComponent {
    private byte command;

    private String address;
    private String replyTo;
    private byte[] data;
    private AMQP.BasicProperties properties;

    @Override
    public void init() throws Exception {
        super.init();

    }

    public static void sendContainerTerminatedCommand(String finishedContainerName, byte exitCode) throws Exception {
        //String address = Constants.HOBBIT_SESSION_ID_FOR_BROADCASTS;
        //String address = Constants.HOBBIT_COMMAND_EXCHANGE_NAME;
        byte command = Commands.DOCKER_CONTAINER_TERMINATED;
        byte[] data = RabbitMQUtils.writeByteArrays(null,
                new byte[][]{RabbitMQUtils.writeString(finishedContainerName)},
                new byte[]{exitCode});
        AMQP.BasicProperties properties = null;
        new CommandSender(null, command, data, properties).send();
    }

    public CommandSender(byte command) {
        this(null, command, null, null);
    }

    public CommandSender(byte command, String dataString) {
        this(null,
                command,
                RabbitMQUtils.writeByteArrays(RabbitMQUtils.writeString(dataString), new byte[][]{}, null),
                null);
    }

    /**
     * @param data must be encoded with {@link RabbitMQUtils#writeByteArrays}
     */
    public CommandSender(byte command, byte[] data) {
        this(null, command, data, null);
    }

    /**
     * @param data must be encoded with {@link RabbitMQUtils#writeByteArrays}
     */
    public CommandSender(String address, byte command, byte[] data, AMQP.BasicProperties properties) {
        this.address = address;
        this.command = command;
        this.data = data;
        this.properties = properties;
    }

    public CommandSender(byte[] data, AMQP.BasicProperties properties, String replyTo) {
        this.replyTo = replyTo;
        this.data = data;
        this.properties = properties;
    }

    @Override
    public void receiveCommand(byte command, byte[] data) {
    }

    @Override
    public void run() throws Exception {
    }

    public void send() throws Exception {
        super.init();
        sendToCmdQueue(address, command, data, properties, replyTo);
        close();
    }

    /**
     * @param data must be encoded with {@link RabbitMQUtils#writeByteArrays}
     */
    private void sendToCmdQueue(String address2, byte command, byte[] data, AMQP.BasicProperties props, String replyTo) throws IOException {
        String address = address2 == null ? getHobbitSessionId() : address2;
        byte addressBytes[] = RabbitMQUtils.writeString(address);
        int addressBytesLength = addressBytes.length;
        int resultLength = addressBytesLength + getByteAmountIn(addressBytesLength) + getByteAmountIn(command);
        boolean attachData = (data != null) && (data.length > 0);
        if (attachData) {
            resultLength += data.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(resultLength);
        buffer.putInt(addressBytesLength);
        buffer.put(addressBytes);
        buffer.put(command);
        if (attachData) {
            buffer.put(data);
        }

        if(replyTo!=null)
            cmdChannel.basicPublish("", replyTo, props, data);
        else {
            if (address2 == null)  //sending command
                address2 = Constants.HOBBIT_COMMAND_EXCHANGE_NAME;
            cmdChannel.basicPublish(address2, "", props, buffer.array());
        } //sending reply top receiver


        //cmdChannel.basicPublish(address, "", props, buffer.array());
        //cmdChannel.basicPublish("", address, MessageProperties.PERSISTENT_BASIC, RabbitMQUtils.writeString(containerName));

    }

    private int getByteAmountIn(byte byteVariable) {
        return Byte.BYTES;
    }

    private int getByteAmountIn(int intVariable) {
        return Integer.BYTES;
    }

}