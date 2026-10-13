package com.example.xmlprocessor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record EmployeeXml(

        @JacksonXmlProperty(isAttribute = true)
        @NotBlank(message = "Employee uid is required")
        String uid,

        @NotBlank(message = "Employee name is required")
        @Size(max = 255)
        String name,

        @Size(max = 255)
        String nameLegal,

        @Size(max = 255)
        String department,

        @Size(max = 255)
        String country,

        @Size(max = 255)
        String state,

        @Size(max = 255)
        String city,

        @Size(max = 50)
        String timeZone,

        @Size(max = 255)
        String company,

        @NotBlank(message = "Employee ID is required")
        @Size(max = 100)
        String employeeId,

        String dTimJoining,

        CommonDataXml commonData

) {
}