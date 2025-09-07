package com.sprocms.jprj.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;

import com.sprocms.jprj.events.packages.pckg.application.UsePackage;

@SpringBootApplication
public class EventsApp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(EventsApp.class, args);
    }

    @Autowired
    UsePackage usePackage;

    @EventListener(ApplicationReadyEvent.class)
    public void StartupEvent() {
        usePackage.prepareAll();
    }
}