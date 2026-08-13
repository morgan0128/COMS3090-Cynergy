package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    // This is the default method used upon running this Application.java
    // You will see this message appear at http://localhost:8080/ -Morgan
    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to Morgan's Experiment 1!";
    }

    // You will see this message appear by inputting a String name parameter
    // at http://localhost:8080/{name}
    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello, " + name + ", and welcome to Morgan's Experiment 1!";
    }
}