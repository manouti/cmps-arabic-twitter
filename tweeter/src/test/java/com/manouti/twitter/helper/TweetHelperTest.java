package com.manouti.twitter.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TweetHelperTest {

    @Test
    void testDecomposeShortTweet() {
        List<String> tweets = TweetHelper.decomposeAndAddHashTags("This is a short tweet.", Collections.emptyMap());
        assertEquals(List.of("This is a short tweet."), tweets);
    }

    @Test
    void testDecomposeLongTweet() {
        List<String> tweets = TweetHelper.decomposeAndAddHashTags("Java is a class-based, object-oriented programming language that is designed to have as few "
                + "implementation dependencies as possible. It is a general-purpose programming language intended to let application developers write once, "
                + "run anywhere (WORA),[16] meaning that compiled Java code can run on all platforms that support Java without the need for recompilation."
                + " Java applications are typically compiled to bytecode that can run on any Java virtual machine (JVM) regardless of the underlying computer "
                + "architecture. The syntax of Java is similar to C and C++, but has fewer low-level facilities than either of them. "
                + "The Java runtime provides dynamic capabilities (such as reflection and runtime code modification) that are typically not available in "
                + "traditional compiled languages. As of 2019, Java was one of the most popular programming languages in use according to GitHub, "
                + "particularly for client-server web applications, with a reported 9 million developers.", Collections.emptyMap());
        assertEquals(List.of("Java is a class-based, object-oriented programming language that is designed to have as few implementation dependencies as possible. It is a general-purpose programming language intended to let application developers write once, run anywhere (WORA),[16] meaning that compiled ",
                "Java code can run on all platforms that support Java without the need for recompilation. Java applications are typically compiled to bytecode that can run on any Java virtual machine (JVM) regardless of the underlying computer architecture. The syntax of Java is similar to C and ",
                "C++, but has fewer low-level facilities than either of them. The Java runtime provides dynamic capabilities (such as reflection and runtime code modification) that are typically not available in traditional compiled languages. As of 2019, Java was one of the most popular ",
                "programming languages in use according to GitHub, particularly for client-server web applications, with a reported 9 million developers."), tweets);
    }

    @Test
    void testAppendHashTags() {
        Map<String, List<String>> hashtags = Map.of("برمجة", List.of("Programming","Software Development"),
                                                   "جافا", List.of("Java"),
                                                    "حاسوب", List.of("Software Development"),
                                                    "جافا سكريبت", List.of("JavaScript"),
                                                    "برامج", List.of("Programming","Software Development"),
                                                    "برنامج", List.of("Programming","Software Development"));
        List<String> updatedTweet = TweetHelper.decomposeAndAddHashTags("This is a tweet with no matching hashtag words", hashtags);
        assertEquals(List.of("This is a tweet with no matching hashtag words"), updatedTweet);

        updatedTweet = TweetHelper.decomposeAndAddHashTags("This is a tweet about Java development", hashtags);
        assertEquals(List.of("This is a tweet about Java development #Java"), updatedTweet);

        updatedTweet = TweetHelper.decomposeAndAddHashTags("This tweet is about both Java and software development", hashtags);
        assertEquals(List.of("This tweet is about both Java and software development #Java #SoftwareDevelopment"), updatedTweet);

        updatedTweet = TweetHelper.decomposeAndAddHashTags("This tweet is about both Java and جافا سكريبت", hashtags);
        assertEquals(List.of("This tweet is about both Java and جافا سكريبت #Java #JavaScript"), updatedTweet);
    }

}
