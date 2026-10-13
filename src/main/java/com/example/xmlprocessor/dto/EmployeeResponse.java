package com.example.xmlprocessor.dto;

import com.example.xmlprocessor.entity.Employee;

import java.time.Instant;
import java.util.UUID;

public record EmployeeResponse(
        Long id,
        UUID uid,
        String employeeId,
        String name,
        String nameLegal,
        String department,
        String country,
        String state,
        String city,
        String timeZone,
        String company,
        Instant dTimJoining,
        Instant dTimCreation,
        Instant dTimLastChange,
        String itemState
) {

    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getUid(),
                employee.getEmployeeCode(),
                employee.getName(),
                employee.getLegalName(),
                employee.getDepartment(),
                employee.getCountry(),
                employee.getState(),
                employee.getCity(),
                employee.getTimeZone(),
                employee.getCompany(),
                employee.getJoiningAt(),
                employee.getSourceCreatedAt(),
                employee.getSourceChangedAt(),
                employee.getItemState()
        );
    }
}