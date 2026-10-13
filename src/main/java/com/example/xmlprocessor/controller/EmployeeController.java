package com.example.xmlprocessor.controller;

import com.example.xmlprocessor.dto.EmployeeResponse;
import com.example.xmlprocessor.service.EmployeeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        value = "/api/employees",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponse> getEmployees(
            @RequestParam(name = "fileId", required = false) UUID fileId) {

        return employeeService.getEmployees(fileId);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getEmployee(@PathVariable("id") Long id) {
        return employeeService.getEmployee(id);
    }
}