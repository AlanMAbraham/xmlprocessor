package com.example.xmlprocessor.repository;

import com.example.xmlprocessor.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    List<Employee> findByUpload_Id(UUID uploadId);
}