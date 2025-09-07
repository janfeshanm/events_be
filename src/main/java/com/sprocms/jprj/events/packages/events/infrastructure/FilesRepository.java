package com.sprocms.jprj.events.packages.events.infrastructure;

import org.springframework.stereotype.Repository;

import com.sprocms.jprj.events.packages.events.domain.Files;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface FilesRepository extends JpaRepository<Files, String> {
    
}
