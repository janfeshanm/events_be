package com.sprocms.jprj.events.packages.events.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    UseEvents useEvents() {
        UseEvents useEvents = new UseEvents();
        return useEvents;
    }
}
