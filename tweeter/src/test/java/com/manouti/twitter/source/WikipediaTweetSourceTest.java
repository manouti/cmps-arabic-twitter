package com.manouti.twitter.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.manouti.twitter.wikipedia.IWikipediaPageManager;

class WikipediaTweetSourceTest {

    @Test
    void testNextTweet() throws Exception {
        IWikipediaPageManager wikipediaPageManager = mock(IWikipediaPageManager.class);
        Optional<Integer> dummyPageId = Optional.of(1);
        when(wikipediaPageManager.getRandomPage()).thenReturn(dummyPageId);
        when(wikipediaPageManager.getPageSummary(dummyPageId.get())).thenReturn("test");
        try (WikipediaTweetSource wikipediaTweetSource = new WikipediaTweetSource(wikipediaPageManager)) {
            String nextTweet = wikipediaTweetSource.nextTweet();
            assertEquals("test", nextTweet);
        }
    }

}
