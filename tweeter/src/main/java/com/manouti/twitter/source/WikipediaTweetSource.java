package com.manouti.twitter.source;

import java.util.Optional;

import com.manouti.twitter.wikipedia.IWikipediaPageManager;

/**
 * A source of tweets that pulls content from Wikipedia.
 *
 * @author manouti
 *
 */
public final class WikipediaTweetSource implements TweetSource {

    private final IWikipediaPageManager wikipediaPageManager;

    public WikipediaTweetSource(IWikipediaPageManager wikipediaPageManager) {
        this.wikipediaPageManager = wikipediaPageManager;
    }

    @Override
    public String nextTweet() throws Exception {
        Optional<Integer> randomPageId = wikipediaPageManager.getRandomPage();
        if (randomPageId.isPresent()) {
            return wikipediaPageManager.getPageSummary(randomPageId.get());
        }
        return null;
    }

}
