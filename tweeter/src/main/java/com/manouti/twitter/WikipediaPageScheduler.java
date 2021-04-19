package com.manouti.twitter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.manouti.twitter.wikipedia.WikipediaPageManager;
import com.manouti.twitter.wikipedia.WikipediaProcessingException;

@Component
class WikipediaPageScheduler {

    private static final int PROGRAMMING_LANGUAGES_CATEGORY_PAGE_ID = 4776;

    private static final Logger log = LoggerFactory.getLogger(WikipediaPageScheduler.class);

    private final WikipediaPageManager wikipediaPageManager;
    private final List<Integer> categoriesList;

    public WikipediaPageScheduler(WikipediaPageManager wikipediaPageManager, Environment environment) throws WikipediaProcessingException {
        this.wikipediaPageManager = wikipediaPageManager;
        String categories = environment.getProperty("wikipedia.categories");
        if (categories != null) {
            categoriesList = Arrays.stream(categories.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        } else {
            categoriesList = List.of(PROGRAMMING_LANGUAGES_CATEGORY_PAGE_ID);
        }
        this.wikipediaPageManager.refreshPages(categoriesList);
    }

    @Scheduled(cron = "0 0 1 * * ?") // at 00:00 on day-of-month 1
    public void refreshWikipediaPages() throws WikipediaProcessingException {
        log.info("Refreshing Wikipedia pages");
        this.wikipediaPageManager.refreshPages(categoriesList);
    }

}
