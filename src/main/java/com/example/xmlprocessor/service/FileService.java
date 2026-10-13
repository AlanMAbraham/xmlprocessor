package com.example.xmlprocessor.service;

import com.example.xmlprocessor.dto.UploadResponse;
import com.example.xmlprocessor.entity.ProcessingStatus;
import com.example.xmlprocessor.entity.UploadedFile;
import com.example.xmlprocessor.exception.ApiException;
import com.example.xmlprocessor.repository.UploadedFileRepository;
import com.example.xmlprocessor.storage.JsonMemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileService {

    private static final Logger log =
            LoggerFactory.getLogger(FileService.class);

    private final UploadLifecycleService lifecycleService;
    private final FileProcessingService processingService;
    private final UploadedFileRepository uploadRepository;
    private final JsonMemoryStore memoryStore;

    public FileService(
            UploadLifecycleService lifecycleService,
            FileProcessingService processingService,
            UploadedFileRepository uploadRepository,
            JsonMemoryStore memoryStore) {

        this.lifecycleService = lifecycleService;
        this.processingService = processingService;
        this.uploadRepository = uploadRepository;
        this.memoryStore = memoryStore;
    }

    public UploadResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "A non-empty XML file is required");
        }

        String filename = file.getOriginalFilename();

        if (filename == null
                || !filename.toLowerCase(Locale.ROOT).endsWith(".xml")) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "File must have an .xml extension");
        }

        // Remove any client-supplied directory path.
        filename = filename.replace('\\', '/');
        filename = filename.substring(filename.lastIndexOf('/') + 1);

        if (filename.length() > 255) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Filename must not exceed 255 characters");
        }

        byte[] content;

        try {
            content = file.getBytes();
        } catch (IOException exception) {
            log.error("Could not read uploaded file", exception);

            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not read uploaded file");
        }

        UUID fileId = lifecycleService.createUpload(filename);

        try {
            processingService.process(fileId, content);
        } catch (TaskRejectedException exception) {
            lifecycleService.failUpload(
                    fileId, "Processing capacity is currently full");

            throw new ApiException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Server is busy. Please retry later");
        }

        log.info("Upload accepted with file ID {}", fileId);

        return new UploadResponse(
                fileId,
                ProcessingStatus.PROCESSING,
                "File accepted. Processing is in progress.");
    }

    public String download(UUID fileId) {
        UploadedFile upload = uploadRepository.findById(fileId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Unknown file ID"));

        if (upload.getStatus() == ProcessingStatus.PROCESSING) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "File is still processing. Retry shortly.");
        }

        if (upload.getStatus() == ProcessingStatus.FAILED) {
            throw new ApiException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Processing failed: " + upload.getErrorMessage());
        }

        return memoryStore.find(fileId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.GONE,
                        "JSON is no longer available in memory. Upload the file again."));
    }
}