package com.manouti.twitter.source;

/**
 * Represents a source of tweet content.
 *
 * @author manouti
 *
 */
public interface TweetSource extends AutoCloseable {

    String nextTweet() throws Exception;

    @Override
    default void close() throws Exception {
    }
}
