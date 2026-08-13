package User_Info.controller;
import User_Info.model.Profile;
import User_Info.model.User_Info;
import User_Info.repository.ProfileRepository;
import User_Info.repository.User_InfoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    ProfileRepository ProfileRepository;
    @Autowired
    User_InfoRepository User_InfoRepository;
    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";
    private String loginFailure = "{\"message\":\"Failed to login: Incorrect password.\"}";
    private Map<String, Object> profileDTO(Profile profile) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("profileId", profile.getProfileId());
        dto.put("profileName", profile.getProfileName());
        dto.put("profileBio", profile.getProfileBio());
        dto.put("age", profile.getAge());
        dto.put("gender", profile.getGender());
        // NEW: Add interest tags to DTO response
        dto.put("interests", profile.getInterests());

        if (profile.getProfilePicture() != null) {
            dto.put("profilePicture",
                    Base64.getEncoder().encodeToString(profile.getProfilePicture()));
        } else {
            dto.put("profilePicture", null);
        }

        return dto;
    }
    private void fillProfileWithData(Profile profileData, Profile profile) {
        if (profileData.hasProfileName()) {
            profile.setProfileName(profileData.getProfileName());
        }
        if (profileData.hasProfileBio()) {
            profile.setProfileBio(profileData.getProfileBio());
        }
        if (profileData.hasAge()) {
            profile.setAge(profileData.getAge());
        }
        if (profileData.hasGender()) {
            profile.setGender(profileData.getGender());
        }
        if (profileData.getInterests() != null) {
            profile.setInterests(profileData.getInterests());
        }
    }
    // EDIT profile
    @Operation(summary = "Edit profile")
    @PutMapping("/edit/profile/{userId}")
    public ResponseEntity<?> editProfile(@PathVariable int userId, @RequestBody Profile profileData) {

        // Find the profile by its ID (same as userId)
        Profile profile = ProfileRepository.findByProfileId(userId);
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Profile does not exist for this user."));
        }
        // Fill in the updated fields
        fillProfileWithData(profile, profileData);
        Profile updated = ProfileRepository.save(profile);
        return ResponseEntity.ok(profileDTO(updated));
    }
    // GET profile
    @Operation(summary = "Get profile by profileId")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable int id) {
        Profile profile = ProfileRepository.findByProfileId(id);
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Profile not found by profileId."));
        }
        return ResponseEntity.ok(profileDTO(profile));
    }
    //  GET all profiles
    @Operation(summary = "Get all profiles")
    @GetMapping // request mapping added
    public List<Map<String, Object>> getAllProfiles() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Profile p : ProfileRepository.findAll()) {
            result.add(profileDTO(p));
        }
        return result;
    }
    // Might be saving profile before saving the user because Profile is the owning side
    @Operation(summary = "Create profile associated with userId (id)")
    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createProfile(@PathVariable int userId, @RequestBody Profile data) {
        Optional<User_Info> userOpt = User_InfoRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User does not exist"));
        }
        User_Info user = userOpt.get();
        // Prevent duplicate profiles
        if (ProfileRepository.findByProfileId(userId) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Profile already exists."));
        }
        Profile profile = new Profile();
        profile.setUser_info(user);   // sets profileId automatically
        fillProfileWithData(data, profile);
        Profile saved = ProfileRepository.save(profile);
        user.setProfile(saved);  // keep relationship consistent
        return ResponseEntity.status(HttpStatus.CREATED).body(profileDTO(saved));

    }
    @Operation(summary = "Delete profile by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted profile",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found by email",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProfile(@RequestParam String emailId) {
        Optional<User_Info> userOpt = User_InfoRepository.findByEmailId(emailId);
        if (userOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found."));
        Profile profile = ProfileRepository.findByProfileId(userOpt.get().getId());
        if (profile == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Profile not found."));
        userOpt.get().unassignProfile();
        ProfileRepository.delete(profile);
        return ResponseEntity.ok(Map.of("status", "success", "email", emailId));
    }

    // profile image test
    @PostMapping("/{userId}/picture")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable int userId, @RequestBody Map<String, String> body) {

        String base64 = body.get("image"); // MUST be "image"
        if (base64 == null)
            return ResponseEntity.badRequest().body("Missing image data");

        Profile profile = ProfileRepository.findByProfileId(userId);
        if (profile == null)
            return ResponseEntity.status(404).body("Profile not found");

        byte[] decoded = Base64.getDecoder().decode(base64);
        profile.setProfilePicture(decoded);

        ProfileRepository.save(profile);
        return ResponseEntity.ok(Map.of("status", "success"));
    }
    @GetMapping("/{userId}/picture")
    public ResponseEntity<?> getProfilePicture(@PathVariable int userId) {

        Profile profile = ProfileRepository.findByProfileId(userId);
        if (profile == null)
            return ResponseEntity.status(404).body("Profile not found");

        if (profile.getProfilePicture() == null)
            return ResponseEntity.ok(Map.of("status", "no_image"));

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // stored as BLOB → returned as JPEG stream
                .body(profile.getProfilePicture());
    }

    @DeleteMapping("/{userId}/picture")
    public ResponseEntity<?> deleteProfilePicture(@PathVariable int userId) {
        Profile profile = ProfileRepository.findByProfileId(userId);

        if (profile == null) return ResponseEntity.status(404).body("Profile not found");

        profile.setProfilePicture(null);
        ProfileRepository.save(profile);

        return ResponseEntity.ok(Map.of("status", "deleted"));
    }
}