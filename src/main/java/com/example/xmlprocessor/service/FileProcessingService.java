package com.example.xmlprocessor.service;

import com.example.xmlprocessor.dto.EmployeesXml;
import com.example.xmlprocessor.storage.JsonMemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FileProcessingService {

    private static final Logger log =
            LoggerFactory.getLogger(FileProcessingService.class);

    private final XmlParserService xmlParserService;
    private final XmlJsonConverter xmlJsonConverter;
    private final UploadLifecycleService lifecycleService;
    private final JsonMemoryStore memoryStore;

    public FileProcessingService(
            XmlParserService xmlParserService,
            XmlJsonConverter xmlJsonConverter,
            UploadLifecycleService lifecycleService,
            JsonMemoryStore memoryStore) {

        this.xmlParserService = xmlParserService;
        this.xmlJsonConverter = xmlJsonConverter;
        this.lifecycleService = lifecycleService;
        this.memoryStore = memoryStore;
    }

    @Async("xmlExecutor")
    public void process(UUID fileId, byte[] content) {
        log.info("Processing started for file {}", fileId);

        try {
            EmployeesXml document = xmlParserService.parse(content);

            String json = xmlJsonConverter.convert(content);

            lifecycleService.completeUpload(fileId, document, json);

            log.info(
                    "Processing completed for file {} with {} employees",
                    fileId,
                    document.employees().size());

        } catch (Exception exception) {
            memoryStore.remove(fileId);

            log.error("Processing failed for file {}", fileId, exception);

            String message = exception instanceof IllegalArgumentException
                    ? exception.getMessage()
                    : "XML processing or employee persistence failed";

            try {
                lifecycleService.failUpload(fileId, message);
            } catch (Exception statusException) {
                log.error(
                        "Could not record FAILED status for file {}",
                        fileId,
                        statusException);
            }
        }
    }
}