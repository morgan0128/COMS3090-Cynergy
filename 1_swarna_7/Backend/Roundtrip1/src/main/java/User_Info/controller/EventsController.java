package User_Info.controller;

import User_Info.repository.Event_Chat_RoomRepository;
import User_Info.service.Chat_RoomService;
import User_Info.service.Map_NodeService;
import User_Info.repository.NotificationsRepository;
import User_Info.service.EventsService;
import User_Info.service.NotificationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import User_Info.model.Events;
import User_Info.model.AdminEventRequest;
import User_Info.model.User_Info;
import User_Info.repository.EventsRepository;
import User_Info.repository.User_InfoRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Events", description = "Operations related to events: event creation, view events, update events, and delete events.")
@RequestMapping("api/events")
public class EventsController {

    @Autowired
    private EventsService eventsService;

    // -Morgan
    @Autowired
    Chat_RoomService Chat_RoomService;

    @Autowired
    Map_NodeService Map_NodeService;

    @Autowired
    Event_Chat_RoomRepository Event_Chat_RoomRepository;

    @Autowired
    EventsRepository EventsRepository;

    // GET all events (all users)
    @Operation(
            summary = "Get all events",
            description = "Returns a list of all events in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    })
    @GetMapping
    public List<Map<String, Object>> getAllEvents() {
        return eventsService.getAllEvents();
    }

    // GET all events for event owner
    @Operation(
            summary = "Get all events owned by a specific user",
            description = "Returns all events created by a specific user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events founds"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}")
    public List<Map<String, Object>> getUserEvents(@Parameter(description = "Unique ID of the user") @PathVariable Integer userId) {
        return eventsService.getUserOwnedEvents(userId);
    }

    // GET all attendees for a specific event
    @Operation(
            summary = "Get attendees for an event",
            description = "Returns all users attending a specific event."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attendees retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{eventId}/attendees")
    public ResponseEntity<Map<String, Object>> getAttendees(@Parameter(description = "Event ID") @PathVariable int eventId) {
        Map<String, Object> result = eventsService.getEventAttendees(eventId);

        if (result.containsKey("status") && "error".equals(result.get("status"))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        return ResponseEntity.ok(result);
    }

    // GET all interested event info for the user
    @Operation(
            summary = "Get interested events for a user",
            description = "Returns the events a user has marked as interested."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Interested events retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}/interested")
    public ResponseEntity<Map<String, Object>> getInterestedEvents(@Parameter(description = "Unique User ID") @PathVariable int userId) {
        Map<String, Object> result = eventsService.getInterestedEvents(userId);

        if ("error".equals(result.get("status"))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        return ResponseEntity.ok(result);
    }

    // GET number of attendees for an event
    @Operation (
            summary = "Count attendees for a specific event",
            description = "Returns the total number of attendees for a specific event."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{eventId}/attendees/count")
    public ResponseEntity<?> countAttendees(@Parameter(description = "Event ID") @PathVariable int eventId) {
        return eventsService.countAttendees(eventId);
    }

    @Operation(
            summary = "Chat room for a specific event",
            description = "Returns the first chatroom for a specific event"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chat room retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("{eventId}/chat")
    public Integer getFirstChatRoomOnEvent(@Parameter(description = "Event ID") @PathVariable int eventId){
        Events e = EventsRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return Event_Chat_RoomRepository.findByEvent(e).get(0).getChatRoom_id();
    }

    // POST new event
    // using wildcard for http response
    @Operation(
            summary = "Create a new event",
            description = "Creates an event for a specific user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createEvent(@Parameter(description = "ID of event owner") @PathVariable Integer userId, @RequestBody Events event) {
        return eventsService.createEvent(userId, event);
    }

    /**
     * adds a user to event attendee table when "interested" button is clicked
     * For event attendees and event attendee notifications
     * ref. EventsServices
     */
    @Operation(
            summary = "Add attendee to event",
            description = "Marks a user as attending or interested in an event."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attendee added successfully"),
            @ApiResponse(responseCode = "404", description = "User or event not found")
    })
    @PostMapping("/{eventId}/attend/{userId}")
    public ResponseEntity<?> addAttendee(@PathVariable int eventId, @PathVariable int userId) {
        return eventsService.addAttendee(eventId, userId);
    }

    // PUT update event
    @Operation(
            summary = "Update an event",
            description = "Updates event details (such as time, description, or location.)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@Parameter(description = "Event ID") @PathVariable int id, @RequestBody Events updatedEvent) {
        return eventsService.updateEvent(id, updatedEvent);
    }

    // DELETE event
    @Operation(
            summary = "Delete an event",
            description = "Deletes a specific event. Event will also delete if user account is deleted."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@Parameter(description = "Event ID") @PathVariable int id, @RequestParam Integer userId) {
        return eventsService.deleteEvent(id, userId);
    }

    @Operation(
            summary = "Remove attendees",
            description = "Removes an attendee when they click the uninterested button for a specific event."
    )
    @DeleteMapping("/{eventId}/attend/{userId}")
    public ResponseEntity<?> removeAttendee(@Parameter(description = "Event ID") @PathVariable int eventId, @Parameter(description = "User ID") @PathVariable int userId) {
        return eventsService.removeAttendee(eventId, userId);
    }

    // takes AdminEventRequest since it is inside, does not create Event object until after approval
    @Operation(
            summary = "Request sponsorship for an event",
            description = "Marks an event as requesting sponsorship. Sends an issue to the admin."
    )
    @PostMapping("/sponsor/request")
    public ResponseEntity<?> requestEventSponsorship(@RequestBody AdminEventRequest request) {
        return eventsService.requestSponsorship(request);
    }

    @Operation(
            summary = "Allows user to invite friend to an interested event",
            description = "Sends an invitation via notification to a user's friend of choice"
    )
    @PostMapping("/{eventId}/invite/{senderId}/{receiverId}")
    public ResponseEntity<?> inviteFriendToEvent(@PathVariable int eventId, @PathVariable int senderId, @PathVariable int receiverId) {
        return eventsService.inviteUser(eventId, senderId, receiverId);
    }

    @Operation(
            summary = "Accept a request send from a friend to attend an event",
            description = "Accepting an invitation adds the user as an attendee and notifies the receiver and sender"
    )
    @PutMapping("/invite/{inviteId}/accept")
    public ResponseEntity<?> acceptEventInvitation(@PathVariable long inviteId) {
        return eventsService.acceptInvite(inviteId);
    }

    @PutMapping("/invite/{inviteId}/decline/{receiverId}")
    public ResponseEntity<?> declineEventInvitation(@PathVariable long inviteId, @PathVariable int receiverId) {
        return eventsService.declineInvite(inviteId, receiverId);
    }

    @GetMapping("/invites/sent/{userId}")
    public ResponseEntity<?> getSentInvitations(@PathVariable int userId) {
        return eventsService.getSentInvites(userId);
    }

    @DeleteMapping("/invite/{inviteId}/cancel/{senderId}")
    public ResponseEntity<?> cancelInvitation(@PathVariable long inviteId, @PathVariable int senderId) {
        return eventsService.cancelInvite(inviteId, senderId);
    }
}