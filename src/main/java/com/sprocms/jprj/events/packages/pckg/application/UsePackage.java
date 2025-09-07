package com.sprocms.jprj.events.packages.pckg.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sprocms.jprj.events.packages.pckg.domain.IPackage;

@Service
public class UsePackage implements IPackage {

    public UsePackage() {
        this.packages = new ArrayList<>();
        this.packages.add(this);
    }

    List<IPackage> packages;

    public String addPackage(IPackage pckg) {
        this.packages.add(pckg);
        return "";
    }

    public void prepare() {

    }

    public void prepareAll() {
        for (IPackage iPackage : packages) {
            iPackage.prepare();
        }
    }
}
