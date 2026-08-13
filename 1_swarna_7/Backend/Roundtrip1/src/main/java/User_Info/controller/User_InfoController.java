package User_Info.controller;

import User_Info.model.User_Info;
import User_Info.service.User_InfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "User Info", description = "Operations related to user and account information: User signup, authentication, editing, and account deletion.")
public class User_InfoController {

    @Autowired
    User_InfoService User_InfoService;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";
    private String loginFailure = "{\"message\":\"Failed to login: Incorrect password.\"}";

    @Operation(
            summary = "Create a user",
            description = "Registers a new user account"
    )
    @PostMapping("/api/signup")
    public Map<String,Object> signup(@RequestBody Map<String,Object> body) {
        return User_InfoService.createUser(body);
    }

    // Currently, essentially a "change password" method; no other Account columns but email_id and user_password
    // Don't want change email feature for now, but would be easy to implement here (by being careful
    // before calling "save()"...)
    @Operation(
            summary="Edit user account",
            description="Updates user account information (such as password and username)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    @PutMapping("/api/edit/{id}")
    public Map<String,Object> edit(@PathVariable int id, @RequestBody Map<String,Object> body) {
        return User_InfoService.editUser(id, body);
    }

    // Switched from GET to POST to avoid id and pw in the endpoint
    @Operation(
            summary = "Authenticate user",
            description = "Verifies login credentials and returns relevant account information"
    )
    @PostMapping("/api/login")
    public Map<String, Object> login(@RequestBody Map<String,Object> body) {
        String emailId = (String) body.get("emailId");
        String userPassword = (String) body.get("userPassword");
        return User_InfoService.userLogin(emailId, userPassword);
    }

    @GetMapping("/api/accounts")
    public List<Map<String,Object>> allUsers() {
        return User_InfoService.getAllUsers();
    }

    // GET specific user
    @Operation (
            summary = "Get user by ID"
    )
    @GetMapping("/api/accounts/{id}")
    public Map<String, Object> getUser(@PathVariable int id) {
        return User_InfoService.getUser(id);
    }

    // EDITED emailId from path variable to request param
    @Operation(
            summary = "Delete a user account",
            description = "Deletes a user account using their emailId"
    )
    @DeleteMapping("/api/delete")
    public ResponseEntity<Map<String, Object>> deleteSignup(@RequestParam String emailId) {
        Map<String, Object> response = User_InfoService.deleteSignup(emailId);

        if ("error".equals(response.get("status"))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

}