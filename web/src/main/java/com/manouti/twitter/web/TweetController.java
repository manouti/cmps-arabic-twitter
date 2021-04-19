package com.manouti.twitter.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TweetController {

    private static final Logger log = LoggerFactory.getLogger(TweetController.class);

    private static final int CONTENT_CHAR_LIMIT = 1000;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @PostMapping(value = "/tweet", consumes = "text/plain;charset=UTF-8")
    public ResponseEntity<String> postTweet(@RequestBody String tweet) {
        if (tweet.length() > CONTENT_CHAR_LIMIT) {
            return ResponseEntity.badRequest().body("Content exceeded maximum character limit of " + CONTENT_CHAR_LIMIT);
        }
        log.info("Queuing tweet: " + tweet);
        amqpTemplate.convertAndSend(tweet);
        return ResponseEntity.ok("Tweet added successfully");
    }

}
