package com.sprocms.jprj.events.packages.events.application;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import com.sprocms.jprj.events.packages.events.domain.Events;
import com.sprocms.jprj.events.packages.events.infrastructure.FilesRepository;
import com.sprocms.jprj.events.packages.pckg.application.UsePackage;
import com.sprocms.jprj.events.packages.pckg.domain.IPackage;

import org.springframework.core.io.Resource;

public class UseEvents implements IPackage {
    @Autowired
    UsePackage usePackage;

    @Autowired
    FilesRepository filesRepository;

    @PostConstruct
    public void postConstruct() {
        usePackage.addPackage(this);
    }

    @Autowired
    BuildProperties buildProperties;

    public Events getPublicEvents() {
        Events Events = new Events();
        Events.setVersion(buildProperties.getVersion());
        return Events;
    }

    public Resource loadAsResource(String filename) throws Exception {
		try {
            Path rootLocation = Paths.get("files");
			Path file = rootLocation.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new Exception(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new Exception("Could not read file: " + filename, e);
		}
	}

    public com.sprocms.jprj.events.packages.events.domain.Files store(MultipartFile file) throws Exception {
		try {
            Path rootLocation = Paths.get("files");
			Path destinationFile = rootLocation.resolve(
					Paths.get(file.getOriginalFilename()))
					.normalize().toAbsolutePath();

			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
                
                    com.sprocms.jprj.events.packages.events.domain.Files fl = new com.sprocms.jprj.events.packages.events.domain.Files();
                    fl.setName(file.getOriginalFilename());
                    filesRepository.save(fl);
                    return fl;

			}
		}
		catch (IOException e) {
			throw new Exception("Failed to store file.", e);
		}
	}

    public void prepare() {

    }
}
