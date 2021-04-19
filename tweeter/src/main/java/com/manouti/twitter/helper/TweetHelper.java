package com.manouti.twitter.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility methods to processing tweet text.
 *
 * @author manouti
 *
 */
public final class TweetHelper {

    private static final int TWEET_CHAR_LIMIT = 280;

    private TweetHelper() {
        throw new AssertionError("No TweetHelper instances for you!");
    }

    public static List<String> decomposeAndAddHashTags(String text, Map<String, List<String>> keywordsArabicToEnglish) {
        List<String> tweets;
        if (text.length() <= TWEET_CHAR_LIMIT) {
            tweets = List.of(text);
        } else {
            tweets = new ArrayList<>();
            String[] lines = text.split("\\r?\\n");
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if ((sb.length() + word.length() + 1) > TWEET_CHAR_LIMIT) {
                        tweets.add(sb.toString());
                        sb.setLength(0);
                    }
                    sb.append(word).append(" ");
                }
                sb.append("\n");
            }
            tweets.add(sb.toString().trim());
        }

        String last = tweets.get(tweets.size() - 1);

        Set<String> hashtagsBasedOnMatchingArabicKeywords = keywordsArabicToEnglish.keySet().stream()
                .filter(s -> text.toLowerCase().contains(s.toLowerCase()))
                .map(s -> keywordsArabicToEnglish.get(s))
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        Set<String> hashtagsBasedOnMatchingEnglishKeywords = keywordsArabicToEnglish.values().stream()
                          .flatMap(List::stream)
                          .filter(s -> text.toLowerCase().contains(s.toLowerCase()))
                          .collect(Collectors.toSet());
        hashtagsBasedOnMatchingArabicKeywords.addAll(hashtagsBasedOnMatchingEnglishKeywords);
        StringBuilder sb = new StringBuilder(last);
        for (String ht : hashtagsBasedOnMatchingArabicKeywords) {
            ht = ht.replace(" ", "");
            if (sb.length() + ht.length() + 2 > TWEET_CHAR_LIMIT) { // 2 accounts for space and # characters
                break;
            }
            sb.append(" #").append(ht);
        }

        tweets = new ArrayList<>(tweets.subList(0, tweets.size() - 1));
        tweets.add(sb.toString());
        return tweets;
    }

}
