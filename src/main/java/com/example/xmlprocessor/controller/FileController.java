package com.example.xmlprocessor.controller;

import com.example.xmlprocessor.dto.UploadResponse;
import com.example.xmlprocessor.service.FileService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UploadResponse> upload(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(fileService.upload(file));
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(
            @RequestParam("fileId") UUID fileId) {

        String json = fileService.download(fileId);

        String disposition = ContentDisposition.attachment()
                .filename(fileId + ".json")
                .build()
                .toString();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .body(json.getBytes(StandardCharsets.UTF_8));
    }
}