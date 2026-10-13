package com.example.xmlprocessor.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "xml_uploads")
public class UploadedFile {

    @Id
    private UUID id;

    @Column(nullable = false, length = 255)
    private String originalFilename;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProcessingStatus status;

    @Column(nullable = false)
    private Instant uploadedAt;

    private Instant completedAt;

    @Column(length = 2000)
    private String errorMessage;

    protected UploadedFile() {
    }

    public UploadedFile(UUID id, String originalFilename) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.status = ProcessingStatus.PROCESSING;
        this.uploadedAt = Instant.now();
    }

    public void markCompleted() {
        this.status = ProcessingStatus.COMPLETED;
        this.completedAt = Instant.now();
        this.errorMessage = null;
    }

    public void markFailed(String message) {
        this.status = ProcessingStatus.FAILED;
        this.completedAt = Instant.now();
        this.errorMessage = message == null
                ? "Processing failed"
                : message.substring(0, Math.min(message.length(), 2000));
    }

    public UUID getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}