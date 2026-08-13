package User_Info.controller;

import User_Info.repository.EventsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import User_Info.model.Events;
import User_Info.service.FriendshipService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Friendship", description = "Operations related to friendships, requests, and friend lists.")
@RequestMapping("api/friends")
public class FriendshipController {
    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private EventsRepository EventsRepository;

    // Sending a request - fixed to match JSON object on frontend
    @Operation(
            summary = "Send friend request",
            description = "Sends a friend request from one user to another."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend request sent successfully."),
            @ApiResponse(responseCode = "404", description = "Invalid user ID/IDs")
    })
    @PostMapping("/request/{userId}/{friendId}")
    public ResponseEntity<Map<String, Object>> sendRequest(@Parameter(description = "User sending the request") @PathVariable int userId, @Parameter(description = "User receiving the request") @PathVariable int friendId) {
        String message = friendshipService.sendRequest(userId, friendId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("friendId", friendId);
        response.put("message", message);
        response.put("status", "PENDING");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Accepting request that was sent - flipped for this reason. check services
    @Operation(
            summary = "Accept friend request",
            description = "Accepts a pending friend request that was sent to that user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend request accepted successfully"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @PostMapping("/accept/{userId}/{friendId}")
    public ResponseEntity<Map<String, Object>> accept(@Parameter(description = "User accepting the request") @PathVariable int userId, @Parameter(description = "User who sent the request") @PathVariable int friendId) {
        String message = friendshipService.acceptRequest(userId, friendId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("friendId", friendId);
        response.put("status", "ACCEPTED");
        response.put("message", message);

        return ResponseEntity.ok(response);
    }

    // Declining request
    @Operation(
            summary = "Decline friend request",
            description = "Declines a pending friend request that was sent to that user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request declined successfully"),
            @ApiResponse(responseCode = "404", description = "Request could not be declined")
    })
    @PostMapping("/decline/{userId}/{friendId}")
    public ResponseEntity<Map<String, Object>> decline(@Parameter(description = "User declining the request") @PathVariable int userId, @Parameter(description = "User who sent the request") @PathVariable int friendId) {
        String message = friendshipService.declineRequest(userId, friendId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("friendId", friendId);
        response.put("status", "REMOVED");
        response.put("message", message);

        HttpStatus status = message.contains("declined") ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    // Remove a friends, can be done by either user
    @Operation(
            summary = "Remove a friend",
            description = "Removes a friendship connection. Either user may initiate."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend removed successfully"),
            @ApiResponse(responseCode = "404", description = "Friendship not found")
    })
    @DeleteMapping("/remove/{userId}/{friendId}")
    public ResponseEntity<Map<String, Object>> removeFriend(@Parameter(description = "User removing the friend") @PathVariable int userId, @Parameter(description = "Friend to remove") @PathVariable int friendId) {
        String message = friendshipService.removeFriend(userId, friendId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("friendId", friendId);
        response.put("status", "REMOVED");
        response.put("message", message);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // GET user's friends
    @Operation(
            summary = "Get user's friends",
            description = "Returns a list of all accepted friends for a specific user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friends retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Unable to find user")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getFriends(@PathVariable int userId) {
        return ResponseEntity.ok(friendshipService.getFriends(userId));
    }

    // GET user's pending requests sent
    @Operation(
            summary = "Get sent friend requests",
            description = "Returns pending friend requests sent by a specific user."
    )
    @GetMapping("/pending/{userId}/sent")
    public ResponseEntity<?> pendingRequestsSent(@PathVariable int userId) {
        return ResponseEntity.ok(friendshipService.getPendingSent(userId));
    }

    // GET user's pending requests received
    @Operation(
            summary = "Get received friend requests",
            description = "Returns pending requests sent TO a specific user."
    )
    @GetMapping("/pending/{userId}/received")
    public ResponseEntity<?> pendingRequestsReceived(@PathVariable int userId) {
        return ResponseEntity.ok(friendshipService.getPendingReceived(userId));
    }

    //Morgan
    @GetMapping("/{userId}/friendsInterestedEvents")
    public ResponseEntity<List<FriendshipService.FriendInterestedEventsDto>> getFriendsInterestedEvents(@PathVariable int userId) {
        List<FriendshipService.FriendInterestedEventsDto> friends = friendshipService.getFriendsInterestedEvents(userId);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/{userId}/friendsAttending/{eventId}")
    public ResponseEntity<?> getFriendsAttendingEvent(@PathVariable int userId, @PathVariable int eventId) {
        if (EventsRepository.findById(eventId).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found associated with that id");
        }
        Events event = EventsRepository.findById(eventId).get();
        List<FriendshipService.FriendAttendingEventDto> friends = friendshipService.getFriendsAttendingEvent(userId, event);
        return ResponseEntity.ok(friends);
    }
}