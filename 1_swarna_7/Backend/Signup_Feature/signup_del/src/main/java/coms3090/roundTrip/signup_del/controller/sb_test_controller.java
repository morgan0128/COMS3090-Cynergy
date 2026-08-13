package coms3090.roundTrip.signup_del.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tanya
 * Controller for testing SB connection with port 8080
 */
@RestController
public class sb_test_controller {
    @GetMapping("/")
    public String index() {
        return "Spring Boot connection: Success";
    }
}
