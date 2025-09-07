package com.sprocms.jprj.events.packages.pckg.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;


public class UsePackageTest {
    @InjectMocks
    UsePackage usePackage;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void AddPackage() {
        usePackage.addPackage(null);
    }
}
