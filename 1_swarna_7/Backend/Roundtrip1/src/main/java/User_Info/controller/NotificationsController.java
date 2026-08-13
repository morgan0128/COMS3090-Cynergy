package User_Info.controller;

import User_Info.model.Notifications;
import User_Info.model.User_Info;
import User_Info.repository.User_InfoRepository;
import User_Info.service.NotificationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@Tag(name = "Notifications", description = "Handles notification creation, retrieval, and status updates.")
@RequestMapping ("api/notifications")
public class NotificationsController {
    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    private User_InfoRepository userRepo;

    // creates notis
    @Operation(
            summary = "Create a notification",
            description = "Creates a notification associated with the specified user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification created successfully."),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    @PostMapping("/{userId}")
    public Notifications create(@Parameter(description = "User ID") @PathVariable int userId, @RequestBody Notifications n) {
        User_Info user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        n.setUser(user);
        return notificationsService.createNotifications(n);
    }

    // get notis for user
    @Operation(
            summary = "Get notifications for a user",
            description = "Retrieves all notifications associated for a specific user."
    )
    @GetMapping("/{userId}")
    public List<Notifications> getUserNotifications(@PathVariable int userId) {
        User_Info user = userRepo.findById(userId).orElseThrow();
        return notificationsService.getUserNotifications(user);
    }

    // mark as read for single noti
    @Operation(
            summary = "Mark notification as read",
            description = "Marks a specific notification as read."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification marked as read."),
            @ApiResponse(responseCode = "404", description = "Notification not found.")
    })
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@Parameter(description = "Notification ID") @PathVariable int notificationId) {
        notificationsService.markAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }

    // clears all notis
    @Operation(
            summary = "Clear all notifications",
            description = "Deletes all notifications for a specific user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All notifications cleared.")
    })
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<String> clearAll(@PathVariable int userId) {
        User_Info user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException(("User not found")));
        notificationsService.clearAll(user);
        return ResponseEntity.ok("All notifications clearedM");
    }
}
