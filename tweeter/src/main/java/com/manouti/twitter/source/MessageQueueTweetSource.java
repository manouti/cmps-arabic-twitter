package com.manouti.twitter.source;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;

/**
 * A source of tweet content that uses a RabbitMQ queueing system.
 *
 * @author manouti
 *
 */
public final class MessageQueueTweetSource implements TweetSource {

    private final ConnectionFactory connectionFactory;
    private final Queue queue;

    public MessageQueueTweetSource(ConnectionFactory connectionFactory, Queue queue) {
        this.connectionFactory = connectionFactory;
        this.queue = queue;
    }

    @Override
    public String nextTweet() throws Exception {
        Connection connection = connectionFactory.createConnection();
        Channel channel = connection.createChannel(false);
        channel.queueDeclare(queue.getName(), true, false, false, null);
        GetResponse response = channel.basicGet(queue.getName(), true);
        String message = null;
        if (response != null) {
            message = new String(response.getBody(), "UTF-8");
        }

        channel.close();
        connection.close();
        return message;
    }

}
