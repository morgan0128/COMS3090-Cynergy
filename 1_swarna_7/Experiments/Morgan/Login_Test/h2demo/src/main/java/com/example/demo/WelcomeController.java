package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {
    
    // You will see this message appear at http://localhost:8080/ -Morgan
    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to Login_Test/h2demo!";
    }

    // You will see this message appear by inputting a String name parameter
    // at http://localhost:8080/{name}
    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello, " + name + ", and welcome to Login_Test/h2demo!";
    }
}