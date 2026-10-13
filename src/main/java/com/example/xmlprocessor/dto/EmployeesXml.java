package com.example.xmlprocessor.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

@JsonRootName(
        value = "employees",
        namespace = "http://www.example.com/employee"
)
public record EmployeesXml(

        @JacksonXmlProperty(isAttribute = true)
        String version,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(
                localName = "employee",
                namespace = "http://www.example.com/employee"
        )
        List<EmployeeXml> employees

) {
}