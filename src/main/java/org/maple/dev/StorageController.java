package org.maple.dev;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Controller
@RequestMapping("/api/files")
public class StorageController {
    private final Path StorageLocation;

    public StorageController(StorageProperties StorageProperties) {
        this.StorageLocation = Paths.get(StorageProperties.getUploadDir()).toAbsolutePath().normalize();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            Path targetLocation = StorageLocation.resolve(fileName);
            file.transferTo(targetLocation);

            String fileDownloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/files/download/").path(fileName).toUriString();

            return ResponseEntity.ok("Upload completed! Download link: " + fileDownloadUrl);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Path filePath = StorageLocation.resolve(fileName).normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        List<String> fileNames;

        try (Stream<Path> pathStream = Files.list(StorageLocation)) {
            fileNames = pathStream.map(Path::getFileName).map(Path::toString).toList();

            return ResponseEntity.ok(fileNames);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
