package com.manouti.twitter.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.manouti.twitter.source.MessageQueueTweetSource;
import com.manouti.twitter.source.WikipediaTweetSource;
import com.manouti.twitter.wikipedia.WikipediaPageManager;

@Configuration
class TweetSourceConfiguration {

    @Bean
    public MessageQueueTweetSource messageQueueTweetSource(ConnectionFactory connectionFactory, Queue queue) {
        return new MessageQueueTweetSource(connectionFactory, queue);
    }

    @Bean
    public WikipediaTweetSource wikipediaTweetSource(WikipediaPageManager wikipediaPageManager) {
        return new WikipediaTweetSource(wikipediaPageManager);
    }

    @Bean
    public WikipediaPageManager wikipediaManager() {
        return new WikipediaPageManager();
    }
}
