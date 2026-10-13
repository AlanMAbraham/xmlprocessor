package com.example.xmlprocessor.service;

import com.example.xmlprocessor.dto.EmployeeResponse;
import com.example.xmlprocessor.exception.ApiException;
import com.example.xmlprocessor.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeResponse> getEmployees(UUID fileId) {
        var employees = fileId == null
                ? employeeRepository.findAll()
                : employeeRepository.findByUpload_Id(fileId);

        return employees.stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    public EmployeeResponse getEmployee(Long id) {
        return employeeRepository.findById(id)
                .map(EmployeeResponse::from)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Employee not found"));
    }
}