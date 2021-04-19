package com.manouti.twitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CmpsArabicBot {

    public static void main(String[] args) {
        SpringApplication.run(CmpsArabicBot.class);
    }

}