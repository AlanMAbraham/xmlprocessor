package com.example.xmlprocessor.dto;

import com.example.xmlprocessor.entity.ProcessingStatus;

import java.util.UUID;

public record UploadResponse(
        UUID fileId,
        ProcessingStatus status,
        String message
) {
}