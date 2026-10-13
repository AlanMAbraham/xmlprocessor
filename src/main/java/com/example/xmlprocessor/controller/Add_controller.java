package com.example.xmlprocessor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Add_controller {

    @GetMapping("/add")
    public String addPage() {
        return "add";
    }
}
