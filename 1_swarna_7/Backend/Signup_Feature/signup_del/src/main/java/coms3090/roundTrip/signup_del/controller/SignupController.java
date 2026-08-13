package coms3090.roundTrip.signup_del.controller;

import coms3090.roundTrip.signup_del.model.Signup;
import coms3090.roundTrip.signup_del.repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * @author tanya
 */
@RestController
@RequestMapping("/api") // Can be changed to /api/auth/ for signup and login later
public class SignupController {

    @Autowired
    SignupRepository signupRepository;
    //private SignupRepository signupRepository; // private instance, better code?

    // GET all users
    @GetMapping("/users")
    public List<Signup> GetAllUsers() {
        return signupRepository.findAll();
    }

    // POST new signup
    @PostMapping("/signup")
    public Signup createUser(@RequestBody Signup signup) {
        return signupRepository.save(signup);
    }

    // Delete feature using unique ID for users to avoid incorrect deletes.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSignup(@PathVariable Integer id) {
        if (signupRepository.existsById(id)) {
            signupRepository.deleteById(id);
            return ResponseEntity.ok("Signup with ID " + id + " deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Signup with ID " + id + " not found.");
        }
    }
}
