package com.manouti.twitter.wikipedia;

import java.util.List;
import java.util.Optional;

public interface IWikipediaPageManager {

    public void refreshPages(List<Integer> categoryPageIds) throws WikipediaProcessingException;

    public Optional<Integer> getRandomPage();

    String getPageSummary(int id) throws WikipediaProcessingException;
}
