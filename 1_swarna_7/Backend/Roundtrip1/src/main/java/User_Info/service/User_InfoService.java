package User_Info.service;

import User_Info.model.User_Info;
import User_Info.repository.User_InfoRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class User_InfoService {
    @Autowired
    User_InfoRepository User_InfoRepository;

    @Autowired
    Chat_RoomService Chat_RoomService;

    @Autowired
    AdminService AdminService;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";
    private String loginFailure = "{\"message\":\"Failed to login: Incorrect password.\"}";


    // Maps the entity first
    private Map<String, Object> userMap(User_Info user) {
        Map<String, Object> dto = new HashMap<>();

        dto.put("id", user.getId());
        dto.put("userName", user.getUserName());
        dto.put ("emailId", user.getEmailId());

        // getImageUrl and imageUrl needs to be added
        // dto.put("profileImageUrl", user.getProfile() != null ? user.getProfile().getImageUrl() : null);

        return dto;
    }

    // Edit username or password
    public Map<String, Object> editUser(int id, Map<String, Object> request) {
        User_Info user = User_InfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.containsKey("userName")) {
            user.setUserName((String) request.get("userName"));
        }
        if (request.containsKey("userPassword")) {
            user.setUserPassword((String) request.get("userPassword"));
        }

        User_InfoRepository.save(user);
        return userMap(user);
    }

    // Login fixed: takes email and password. Uses POST instead of GET to check info, avoid adding the email and password in the endpoint
    public Map<String, Object> userLogin(String email, String pw) {
        User_Info user = User_InfoRepository.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("Account with this ID does not exist"));

        if (!user.getUserPassword().equals(pw)) {
            throw new RuntimeException("Incorrect password");
        }

        Map<String, Object> dto = new HashMap<>();
        dto.put("id", user.getId());
        dto.put("userName", user.getUserName());
        dto.put("emailId", user.getEmailId());

        return dto;
    }

//    public Map<String, Object> userLogin(String emailId, String pw) {
//        User_Info user = User_InfoRepository.findByEmailId(emailId)
//                .orElseThrow(() -> new RuntimeException("Account does not exist"));
//        if (!user.getUserPassword().equals(pw)) {
//            throw new RuntimeException("Incorrect password");
//        }
//
//        return userMap(user);
//    }

    // Get ALL users
    public List<Map<String, Object>> getAllUsers() {
        List<User_Info> users = User_InfoRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (User_Info user : users) {
            result.add(userMap(user));
        }
        return result;
    }

    public Map<String, Object> getUser(int id) {
        User_Info user = User_InfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> dto = new HashMap<>();
        dto.put("id", user.getId());
        dto.put("userName", user.getUserName());
        dto.put("emailId", user.getEmailId());
        // add profile image URL here later

        return dto;
    }

    // Create an account/signup
    public Map<String, Object> createUser(Map<String, Object> signup) {
        String emailId = (String) signup.get("emailId");
        String name = (String) signup.get("userName");
        String password = (String) signup.get("userPassword");

        if (User_InfoRepository.findByEmailId(emailId).isPresent()) {
            throw new RuntimeException("Account already exists with the emailId!");
        }

        User_Info user = new User_Info();
        user.setEmailId(emailId);
        user.setUserName(name != null ? name : "New User");
        user.setUserPassword(password);

        User_Info saved = User_InfoRepository.save(user);

        Chat_RoomService.addToAllPublicChatRoom(saved);

        return userMap(saved);
    }

    public User_Info createUserWithParam(String email, String username, String password) {
        if (User_InfoRepository.findByEmailId(email).isPresent()) {
            throw new RuntimeException("Account already exists with that email!");
        }

        User_Info usr = new User_Info(email, username, password);
        User_Info savedUsr = User_InfoRepository.save(usr);
        Chat_RoomService.addToAllPublicChatRoom(usr);
//        if (savedUsr.getId() == 1){
//            AdminService.setAdmin(1);
//        }
        return savedUsr;
    }


    public Map<String, Object> deleteSignup(String emailId) {
        Optional<User_Info> userOpt = User_InfoRepository.findByEmailId(emailId);
        Map<String, Object> response = new HashMap<>();

        if (userOpt.isPresent()) {
            User_InfoRepository.delete(userOpt.get());
            response.put("status", "success");
            response.put("emailId", emailId);
        } else {
            response.put("status", "error");
            response.put("message", "Email '" + emailId + "' not found.");
        }
        return response;
    }


//    Optional<User_Info> serviceFindByEmailId(String emailId){
//        return User_InfoRepository.findByEmailId(emailId);
//    }

    // NEED TO CHECK
    public Optional<User_Info> serviceFindById(int id){
        return User_InfoRepository.findById(id);
    }
}
