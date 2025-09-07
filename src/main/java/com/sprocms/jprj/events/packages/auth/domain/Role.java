package com.sprocms.jprj.events.packages.auth.domain;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @UuidGenerator
    private String id;

    @Column
    private String name;

    @Column
    private String description;

    // Getter for id
    public String getId() {
        return id;
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Role() {
    }

    // Setter for id
    public void setId(String id) {
        this.id = id;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for description
    public String getDescription() {
        return description;
    }

    // Setter for description
    public void setDescription(String description) {
        this.description = description;
    }
}