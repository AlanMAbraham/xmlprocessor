package com.example.xmlprocessor.dto;

import jakarta.validation.constraints.Size;

public record CommonDataXml(
        String dTimCreation,
        String dTimLastChange,

        @Size(max = 50)
        String itemState
) {
}