package com.example.xmlprocessor.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "xml_employees",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_xml_employee_upload_uid",
        columnNames = {"upload_id", "employee_uid"}
    )
)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "upload_id", nullable = false)
    private UploadedFile upload;

    @Column(name = "employee_uid", nullable = false)
    private UUID uid;

    @Column(nullable = false, length = 100)
    private String employeeCode;

    @Column(nullable = false, length = 255)
    private String name;

    private String legalName;
    private String department;
    private String country;
    private String state;
    private String city;

    @Column(length = 50)
    private String timeZone;

    private String company;

    private Instant joiningAt;
    private Instant sourceCreatedAt;
    private Instant sourceChangedAt;

    @Column(length = 50)
    private String itemState;

    protected Employee() {
    }

    public Employee(
            UploadedFile upload,
            UUID uid,
            String employeeCode,
            String name,
            String legalName,
            String department,
            String country,
            String state,
            String city,
            String timeZone,
            String company,
            Instant joiningAt,
            Instant sourceCreatedAt,
            Instant sourceChangedAt,
            String itemState) {

        this.upload = upload;
        this.uid = uid;
        this.employeeCode = employeeCode;
        this.name = name;
        this.legalName = legalName;
        this.department = department;
        this.country = country;
        this.state = state;
        this.city = city;
        this.timeZone = timeZone;
        this.company = company;
        this.joiningAt = joiningAt;
        this.sourceCreatedAt = sourceCreatedAt;
        this.sourceChangedAt = sourceChangedAt;
        this.itemState = itemState;
    }

    public Long getId() {
        return id;
    }

    public UploadedFile getUpload() {
        return upload;
    }

    public UUID getUid() {
        return uid;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getName() {
        return name;
    }

    public String getLegalName() {
        return legalName;
    }

    public String getDepartment() {
        return department;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getCompany() {
        return company;
    }

    public Instant getJoiningAt() {
        return joiningAt;
    }

    public Instant getSourceCreatedAt() {
        return sourceCreatedAt;
    }

    public Instant getSourceChangedAt() {
        return sourceChangedAt;
    }

    public String getItemState() {
        return itemState;
    }
}