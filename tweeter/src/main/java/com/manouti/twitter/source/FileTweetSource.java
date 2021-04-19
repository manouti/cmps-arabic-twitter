package com.manouti.twitter.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sources tweet content from a file.
 *
 * @author manouti
 *
 */
public final class FileTweetSource implements TweetSource {

    private static final Logger log = LoggerFactory.getLogger(FileTweetSource.class);

    private final BufferedReader br;

    public FileTweetSource(String path) throws UnsupportedEncodingException {
        InputStream fis = FileTweetSource.class.getResourceAsStream(path);
        InputStreamReader isr = new InputStreamReader(fis, "utf-8");
        br = new BufferedReader(isr);
    }

    @Override
    public String nextTweet() {
        try {
            return br.readLine();
        } catch (IOException e) {
            log.error("Error reading next tweet from source", e);
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        br.close();
    }

}
