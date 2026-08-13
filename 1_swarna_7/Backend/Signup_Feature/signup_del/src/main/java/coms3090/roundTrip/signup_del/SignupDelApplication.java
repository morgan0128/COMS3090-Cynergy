package coms3090.roundTrip.signup_del;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanya
 * Application for Signup and Delete - Roundtrip 1
 */

@SpringBootApplication
public class SignupDelApplication {

	public static void main(String[] args) {
		SpringApplication.run(SignupDelApplication.class, args);
	}
}