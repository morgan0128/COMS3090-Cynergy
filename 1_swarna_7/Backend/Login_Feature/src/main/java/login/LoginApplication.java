package login;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LoginApplication {

	public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class, args);
    }

    // Explicitly for testing purposes
    @Bean
    CommandLineRunner initTestAccount(AccountRepository AccountRepository) {
        return args -> {
            Account Person1 = new Account("gromplvr94@gmail.com", "hateJglr");
            Account Person2 = new Account("johnbonham22@gmail.com", "ledzep7");
            Account Person3 = new Account("professorG@gmail.com", "Unagi");
            AccountRepository.save(Person1);
            AccountRepository.save(Person2);
            AccountRepository.save(Person3);
        };
    }
}
