package com.shad0wowl.taskmanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class TestController {
    
    @GetMapping("/api/test")
    public String test() {
        return "TaskManager backend is running";
    }
}
