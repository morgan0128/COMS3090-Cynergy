package coms309;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PetClinic Spring Boot Application.
 * 
 * @author Vivek Bengre
 */

@SpringBootApplication
public class Application {

    // The main method, which may launch our application via Springboot.
    // You can view where the application's content is displayed
    // from in WelcomeController.java -Morgan
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}
