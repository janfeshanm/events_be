package com.sprocms.jprj.events.packages.events.presentation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sprocms.jprj.events.Utils;
import com.sprocms.jprj.events.packages.events.application.UseEvents;
import com.sprocms.jprj.events.packages.events.domain.Files;
import com.sprocms.jprj.events.packages.events.domain.Events;

@RestController
public class PublicAPI {
    @Autowired
    UseEvents useEvents;

    @PostMapping("/p/ci/fe")
    ResponseEntity<String> newFE(@RequestPart(value = "file") final MultipartFile uploadfile,
            @RequestHeader Map<String, String> headers) throws IOException {
        // String passCode = headers.get("passcode");
        // if (!passCode.equals("123456")) {
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        // }
        final byte[] bytes = uploadfile.getBytes();
        String filePathString = Utils.getFilesFolderPath() + "/spa.zip";
        final Path path = Paths.get(filePathString);
        File f = new File(filePathString);
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
        java.nio.file.Files.write(path, bytes);
        Utils.unzip(filePathString);

        return ResponseEntity.ok("uploaded");
    }

    @GetMapping("/p/Events")
    ResponseEntity<Events> get() {
        return ResponseEntity.ok().body(useEvents.getPublicEvents());
    }

    @GetMapping("/p/Events/error500")
    ResponseEntity<String> error() {
        return ResponseEntity.status(500).body("some error message");
    }

    @GetMapping("/p/Events/noError")
    ResponseEntity<String> noError() {
        return ResponseEntity.ok().body("no error4");
    }
    @GetMapping("/p/Events/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Resource file;
            file = useEvents.loadAsResource(filename);
		return ResponseEntity.ok().contentType(MediaTypeFactory.getMediaType(file).orElse(MediaType.ALL) ).contentLength(file.contentLength()).body(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
return ResponseEntity.notFound().build();
		
	}

    @PostMapping("/p/Events/files")
	public ResponseEntity<Files> handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		System.out.println(file.getSize());
        try {
            return ResponseEntity.ok().body(useEvents.store(file));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).body(null);
	}
}
