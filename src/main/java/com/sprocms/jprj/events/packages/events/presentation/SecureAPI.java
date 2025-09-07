package com.sprocms.jprj.events.packages.events.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sprocms.jprj.events.packages.events.application.UseEvents;
import com.sprocms.jprj.events.packages.events.domain.Events;

@RestController
public class SecureAPI {
    @Autowired
    UseEvents useEvents;

    @GetMapping("/s/Events")
    ResponseEntity<Events> get() {
        return ResponseEntity.ok().body(useEvents.getPublicEvents());
    }
}
