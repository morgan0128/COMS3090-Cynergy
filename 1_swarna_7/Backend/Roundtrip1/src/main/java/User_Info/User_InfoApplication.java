package User_Info;

import User_Info.model.Events;
import User_Info.model.Priv_Chat_Room;
import User_Info.repository.EventsRepository;
import User_Info.repository.User_InfoRepository;
import User_Info.model.User_Info;
import User_Info.controller.User_InfoController;
import User_Info.service.Chat_RoomService;
import User_Info.service.EventsService;
import User_Info.service.User_InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // scheduling for event countdown
public class User_InfoApplication {
	public static void main(String[] args) {
        SpringApplication.run(User_InfoApplication.class, args);
    }



// Explicitly for testing purposes
//    @Autowired
//    User_InfoService User_InfoService;
//
//    @Autowired
//    EventsService EventsService;
//
//    @Bean
//    CommandLineRunner initUser_Info(User_InfoRepository User_InfoRepository) {
//        return args -> {
//
//            User_Info Person1 = User_InfoService.createUserWithParam("professorG@gmail.com", "ross", "Unagi");
//            ResponseEntity<?> e1 = EventsService.createEvent(Person1.getId(), new Events());
//            ResponseEntity<?> e2 = EventsService.createEvent(Person1.getId(), new Events());
//
//            User_Info Person2 = User_InfoService.createUserWithParam("johnbonham22@gmail.com", "john", "zeppelin");
//            User_Info Person3 = User_InfoService.createUserWithParam("gromplvr94@gmail.com", "gromp", "hateJglr");
//
//            ResponseEntity<?> e3 = EventsService.createEvent(Person1.getId(), new Events());
//            ResponseEntity<?> e4 = EventsService.createEvent(Person1.getId(), new Events());
//
//        };
//    }

}

