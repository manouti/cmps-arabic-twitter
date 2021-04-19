package com.manouti.twitter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.manouti.twitter.helper.TweetHelper;
import com.manouti.twitter.source.TweetSource;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

@Component
class TweetScheduler {

    private static final Logger log = LoggerFactory.getLogger(TweetScheduler.class);

    private final List<TweetSource> tweetSources;
    private final Map<String, List<String>> keywordsArabicToEnglish = new HashMap<>();

    public TweetScheduler(List<TweetSource> tweetSources, Environment environment) {
        this.tweetSources = tweetSources;
        String[] keywordsAr = environment.getProperty("tweet.keywords.arabic").split(";");
        String[] keywordsEn = environment.getProperty("tweet.keywords.english").split(";");
        if (keywordsAr.length != keywordsEn.length) {
            throw new IllegalArgumentException("Arabic and English keywords do not match in length");
        }
        for (int i = 0; i < keywordsAr.length; i++) {
            keywordsArabicToEnglish.put(keywordsAr[i], Arrays.asList(keywordsEn[i].split(",")));
        }
    }

    @Scheduled(fixedRateString = "${tweet.fixedRate.in.milliseconds}")
    public void tweet() {
        tweetSources.forEach(this::tweetFromSource);
    }

    private void tweetFromSource(TweetSource tweetSource) {
        String line = null;
        try {
            line = tweetSource.nextTweet();
        } catch (Exception e) {
            log.error("Error getting next tweet", e);
        }
        if (line != null && !line.isBlank()) {
            log.info("Sending tweet: " + line);
            sendTweet(line);
        }
    }

    private void sendTweet(String text) {
        Twitter twitter = TwitterFactory.getSingleton();
        try {
            List<String> decomposedTweet = TweetHelper.decomposeAndAddHashTags(text, keywordsArabicToEnglish);

            long inReplyToStatusId = -1;
            int counter = 0;
            int threadLimit = 5;

            while (counter < Math.min(decomposedTweet.size(), threadLimit)) {
                StatusUpdate statusUpdate = new StatusUpdate(decomposedTweet.get(counter));
                statusUpdate.setInReplyToStatusId(inReplyToStatusId);

                Status updatedStatus = twitter.updateStatus(statusUpdate);
                inReplyToStatusId = updatedStatus.getId();
                counter++;
            }
        } catch (TwitterException e) {
            log.error("Error tweeting", e);
        }
    }

    @PreDestroy
    private void preDestroy() {
        for (TweetSource tweetSource : tweetSources) {
            try {
                tweetSource.close();
            } catch (Exception e) {
                log.warn("Error while closing tweet source", e);
            }
        }
    }
}
