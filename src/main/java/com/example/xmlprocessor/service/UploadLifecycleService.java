package com.example.xmlprocessor.service;

import com.example.xmlprocessor.dto.EmployeesXml;
import com.example.xmlprocessor.entity.Employee;
import com.example.xmlprocessor.entity.UploadedFile;
import com.example.xmlprocessor.repository.EmployeeRepository;
import com.example.xmlprocessor.repository.UploadedFileRepository;
import com.example.xmlprocessor.storage.JsonMemoryStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UploadLifecycleService {

    private final UploadedFileRepository uploadRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final JsonMemoryStore memoryStore;

    public UploadLifecycleService(
            UploadedFileRepository uploadRepository,
            EmployeeRepository employeeRepository,
            EmployeeMapper employeeMapper,
            JsonMemoryStore memoryStore) {

        this.uploadRepository = uploadRepository;
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.memoryStore = memoryStore;
    }

    @Transactional
    public UUID createUpload(String filename) {
        UUID fileId = UUID.randomUUID();

        UploadedFile upload = new UploadedFile(fileId, filename);
        uploadRepository.save(upload);

        return fileId;
    }

    @Transactional
    public void completeUpload(
            UUID fileId,
            EmployeesXml document,
            String json) {

        UploadedFile upload = uploadRepository.findById(fileId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Upload not found"));

        List<Employee> employees = document.employees().stream()
                .map(employee -> employeeMapper.toEntity(employee, upload))
                .toList();

        long uniqueUids = employees.stream()
                .map(Employee::getUid)
                .distinct()
                .count();

        if (uniqueUids != employees.size()) {
            throw new IllegalArgumentException(
                    "Duplicate employee uid within uploaded XML");
        }

        employeeRepository.saveAllAndFlush(employees);

        memoryStore.save(fileId, json);
        upload.markCompleted();
    }

    @Transactional
    public void failUpload(UUID fileId, String errorMessage) {
        UploadedFile upload = uploadRepository.findById(fileId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Upload not found"));

        upload.markFailed(errorMessage);
    }
}