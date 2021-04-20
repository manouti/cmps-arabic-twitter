# Overview

This repo hosts the code of the Twitter bot that randomly sends tweets about Programming related information in Arabic. There are different configurable sources from which the tweet content can be obtained:

- a file source
- a messaging queue source
- a Wikipedia API source

# Building

The code requires Java 11. For more information on Java, or to download it, please check https://openjdk.java.net/

# Configuration

There are two main pieces of configuration:

- schedule properties: holds a property containing how often to run the scheduled bot, which will query the available tweet sources (by default it runs once a day)
- application properties: you will find here properties containing Arabic keywords (and their corresponding English versions in the same order), which will be used to append hashtags. It also holds the default page ID of the Wikipedia category whose fetched pages belong to (by default the Programming Languages category at https://ar.wikipedia.org/wiki/%D8%AA%D8%B5%D9%86%D9%8A%D9%81:%D9%84%D8%BA%D8%A7%D8%AA_%D8%A8%D8%B1%D9%85%D8%AC%D8%A9
