package com.sprocms.jprj.events.packages.events.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.info.BuildProperties;

import com.sprocms.jprj.events.packages.events.domain.Events;

public class EventsServicesTest {
    @InjectMocks
    UseEvents useEvents;

    @Mock
    BuildProperties mockedBuildProperties;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        when(mockedBuildProperties.getVersion()).thenReturn("0.0.1");
    }

    @Test
    public void GetPublicEvents() {
        Events Events = useEvents.getPublicEvents();
        assertEquals(Events.getVersion(), "0.0.1");
    }

    @Test
    void Prepare() {
        useEvents.prepare();
    }
}
