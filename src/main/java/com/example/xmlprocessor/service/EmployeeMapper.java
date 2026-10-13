package com.example.xmlprocessor.service;

import com.example.xmlprocessor.dto.CommonDataXml;
import com.example.xmlprocessor.dto.EmployeeXml;
import com.example.xmlprocessor.entity.Employee;
import com.example.xmlprocessor.entity.UploadedFile;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    private final Validator validator;

    public EmployeeMapper(Validator validator) {
        this.validator = validator;
    }

    public Employee toEntity(EmployeeXml source, UploadedFile upload) {
        if (source == null) {
            throw new IllegalArgumentException(
                    "Employee record must not be empty");
        }

        validate(source);

        CommonDataXml common = source.commonData();

        if (common != null) {
            validate(common);
        }

        UUID uid = parseUuid(source.uid());

        return new Employee(
                upload,
                uid,
                source.employeeId().trim(),
                source.name().trim(),
                source.nameLegal(),
                source.department(),
                source.country(),
                source.state(),
                source.city(),
                source.timeZone(),
                source.company(),
                parseDate(source.dTimJoining(), "dTimJoining"),
                common == null ? null
                        : parseDate(common.dTimCreation(), "dTimCreation"),
                common == null ? null
                        : parseDate(common.dTimLastChange(), "dTimLastChange"),
                common == null ? null : common.itemState()
        );
    }

    private <T> void validate(T value) {
        var violations = validator.validate(value);

        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(violation ->
                            violation.getPropertyPath()
                                    + ": " + violation.getMessage())
                    .sorted()
                    .collect(Collectors.joining("; "));

            throw new IllegalArgumentException(message);
        }
    }

    private UUID parseUuid(String value) {
        try {
            String text = value.trim();
            UUID uuid = UUID.fromString(text);

            if (!uuid.toString().equalsIgnoreCase(text)) {
                throw new IllegalArgumentException();
            }

            return uuid;
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Employee uid must be a valid UUID");
        }
    }

    private Instant parseDate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Instant.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    fieldName + " must be a valid ISO-8601 timestamp");
        }
    }
}