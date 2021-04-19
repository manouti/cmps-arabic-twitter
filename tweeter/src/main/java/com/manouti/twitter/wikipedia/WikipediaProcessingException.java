package com.manouti.twitter.wikipedia;

/**
 * Signals an error processing Wikipedia content.
 *
 * @author manouti
 *
 */
public class WikipediaProcessingException extends Exception {

    private static final long serialVersionUID = 6845504368810638441L;

    public WikipediaProcessingException(Throwable cause) {
        super(cause);
    }

    public WikipediaProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
