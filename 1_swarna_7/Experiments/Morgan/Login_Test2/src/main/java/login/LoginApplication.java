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

        @Bean
        CommandLineRunner initTestAccount(testAccountRepository testAccountRepository) {
            return args -> {
                testAccount Person1 = new testAccount("gromplvr94@gmail.com", "hateJglr");
                testAccount Person2 = new testAccount("johnbonham22@gmail.com", "ledzep7");
                testAccount Person3 = new testAccount("professorG@gmail.com", "Unagi");
                testAccountRepository.save(Person1);
                testAccountRepository.save(Person2);
                testAccountRepository.save(Person3);

            };
        }



}
