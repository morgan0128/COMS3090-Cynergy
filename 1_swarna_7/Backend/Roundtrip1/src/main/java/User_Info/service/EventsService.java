package User_Info.service;

import User_Info.model.*;
import User_Info.repository.Event_InvitationRepository;
import User_Info.repository.EventsRepository;
import User_Info.repository.User_InfoRepository;

import jdk.jfr.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.Time;
import java.util.*;

import static User_Info.model.EventInvitation.Status.*;

// for reference when implementing: List for ordered collection and don't care about dupes, for notis
// Set has hash based uniqueness and avoids duplicate rows, for attendees

@Service
public class EventsService {

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private User_InfoRepository userInfoRepository;

    @Autowired
    private Event_InvitationRepository inviteRepo;

    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    Chat_RoomService Chat_RoomService;

    @Autowired
    Map_NodeService Map_NodeService;

    @Autowired
    private AdminService adminService;

    private Map<String, Object> eventDTO(Events e) {
        Map<String, Object> dto = new HashMap<>();

        dto.put("id", e.getId());
        dto.put("eventName", e.getEventName());
        dto.put("description", e.getDescription());
        dto.put("eventLocation", e.getEventLocation());
        dto.put("eventDate", e.getEventDate());
        dto.put("eventTime", e.getEventTime());
        dto.put("ownerId", e.getOwner().getId());
        dto.put("attendeeCount", e.getAttendees().size());
        dto.put("tags", e.getTags());

        // to prevent recursion
        if (e.getOwner() != null) {
            Map<String, Object> owner = new HashMap<>();
            owner.put("id", e.getOwner().getId());
            owner.put("userName", e.getOwner().getUserName());
            dto.put("owner", owner);
        } else {
            dto.put("owner", null);
        }

        // attendee count
        dto.put("attendeeCount",
                e.getAttendees() != null ? e.getAttendees().size() : 0
        );

        // event interest recommendations (we are using the same tags used in profile interest)
        dto.put("tags", e.getTags() != null ? e.getTags() : List.of());

        // sponsored event - sponsor request
        Map<String, Object> sponsorship = new HashMap<>();
        sponsorship.put("requested", e.isSponsoredRequested());
        sponsorship.put("approved", e.isSponsoredApproved());
        sponsorship.put("sponsorName", e.getSponsorName());
        dto.put("sponsored", sponsorship);

        // invited friends - finishing up
        dto.put("invitedUsers", List.of());

        // reviews
        List<Map<String, Object>> reviewList = new ArrayList<>();
        for (Review r : e.getReviews()) {
            reviewList.add(Map.of(
                    "rating", r.getRating(),
                    "comment", r.getComment(),
                    "userId", r.getUser().getId(),
                    "createdAt", r.getCreatedAt()
            ));
        }
        dto.put("reviews", reviewList);

        return dto;
    }

    // to GET all event
    public List<Map<String, Object>> getAllEvents() {
        return eventsRepository.findAll()
                .stream()
                .map(this::eventDTO)
                .toList();
    }

    // GET events by owner
    public List<Map<String, Object>> getUserOwnedEvents(int userId) {
        return eventsRepository.findByOwnerId(userId)
                .stream()
                .map(this::eventDTO)
                .toList();
    }

    // GET attendees for an event
    public Map<String, Object> getEventAttendees(int eventId) {

        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<Map<String, Object>> attendeeList = event.getAttendees().stream()
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("userName", u.getUserName());
                    map.put("emailId", u.getEmailId());
                    return map;
                })
                .toList();

        return Map.of(
                "eventId", event.getId(),
                "eventName", event.getEventName(),
                "attendeeCount", attendeeList.size(),
                "attendees", attendeeList
        );
    }

    // GET events a specific user is attending
    // Cleaned up for frontend response
    public Map<String, Object> getInterestedEvents(int userId) {
        User_Info user = userInfoRepository.findById(userId).orElse(null);

        if (user == null) {
            return Map.of("status", "error", "message", "User not found");
        }

        List<Map<String, Object>> attending = user.getAttendingEvents()
                .stream()
                .map(this::eventDTO)
                .toList();

        return Map.of(
                "userId", userId,
                "status", "success",
                "attendingEvents", attending
        );
    }

    public ResponseEntity<?> createEvent(int userId, Events event) {
        Optional<User_Info> ownerOpt = userInfoRepository.findById(userId);

        if (ownerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
        }

        User_Info owner = ownerOpt.get();
        event.setOwner(owner);

        Events saved = eventsRepository.save(event);

        Chat_RoomService.createEventRoom(saved);
        notificationsService.notifyUsersBasedOnInterest(saved); // triggers notification to interested users
        notificationsService.notifyEvent(owner, saved,
                "Your event '" + saved.getEventName() + "' has been created!");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("status", "success", "event", eventDTO(saved)));
    }

    // Used internally in backend only - Morgan
    public ResponseEntity<Events> createEventFromRequest(int userId, Events event) {
        Optional<User_Info> userOpt = userInfoRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        User_Info owner = userOpt.get();
        event.setOwner(owner);
        Events e = eventsRepository.save(event);

        Chat_RoomService.createEventRoom(e);
        notificationsService.notifyEvent(owner, e,
                "Event " + e.getEventName() + " has been published.");

        return ResponseEntity.status(HttpStatus.CREATED).body(e);
    }

    // Add attendee
    public ResponseEntity<?> addAttendee(int eventId, int userId) {
        Events event = eventsRepository.findById(eventId).orElse(null);
        User_Info user = userInfoRepository.findById(userId).orElse(null);

        if (event == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User or event not found"));
        }

        if (!event.getAttendees().add(user)) {
            return ResponseEntity.ok(Map.of(
                    "status", "warning",
                    "message", "User already attending"
            ));
        }

        eventsRepository.save(event);
        notificationsService.notifyEvent(user, event,
                "You joined event '" + event.getEventName() + "'");

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "event", eventDTO(event)
        ));
    }

    // Remove attendee
    public ResponseEntity<?> removeAttendee(int eventId, int userId) {
        Events event = eventsRepository.findById(eventId).orElse(null);
        User_Info user = userInfoRepository.findById(userId).orElse(null);

        if (event == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User or event not found"));
        }

        event.getAttendees().remove(user);
        eventsRepository.save(event);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "event", eventDTO(event)
        ));
    }

    // update an event
    public ResponseEntity<?> updateEvent(int eventId, Events updatedEvent) {
        Events existing = eventsRepository.findById(eventId).orElse(null);

        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Event not found"));
        }

        existing.setEventName(updatedEvent.getEventName());
        existing.setEventLocation(updatedEvent.getEventLocation());
        existing.setEventDate(updatedEvent.getEventDate());
        existing.setEventTime(updatedEvent.getEventTime());

        Events saved = eventsRepository.save(existing);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "event", eventDTO(saved)
        ));
    }

    // NEW IMPLEMENTATION for DELETE
    public ResponseEntity<String> deleteEvent(int eventId, int userId) {
        Events event = eventsRepository.findById(eventId).orElse(null);

        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }

        if (event.getOwner().getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to delete this event.");
        }

        Map_NodeService.handleRemoveEventFromAssocNodes(event);
        eventsRepository.delete(event);

        return ResponseEntity.ok("Event deleted");
    }

    // Counts the number of attendees
    public ResponseEntity<Map<String, Object>> countAttendees(int eventId) {
        Events event = eventsRepository.findById(eventId).orElse(null);

        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "error",
                            "message", "Event not found",
                            "eventId", eventId
                    ));
        }

        // Use eventDTO to avoid duplicating logic
        Map<String, Object> dto = eventDTO(event);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "eventId", eventId,
                "attendeeCount", dto.get("attendeeCount")
        ));
    }

    public ResponseEntity<?> requestSponsorship(AdminEventRequest request) {
        // to validate that sponsorId belongs to a Tier.SPONSOR admin
        boolean validSponsor = adminService.validateAdminEventRequestSponsor(request);
        if (!validSponsor) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("status", "error",
                            "message", "Invalid sponsorId — must be a SPONSOR-tier admin")
            );
        }

        // open an issue for admin approval
        Admin_Issue issue = adminService.openIssueApproveEvent(request);

        if (issue == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("status", "error",
                            "message", "Sponsor must be Tier.SPONSOR")
            );
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Sponsorship request submitted for approval.",
                "issueId", issue.getIssue_id(),
                "proposedEvent", request
        ));
    }

    public ResponseEntity<?> inviteUser(int eventId, int senderId, int receiverId) {

        Events event = eventsRepository.findById(eventId).orElse(null);
        User_Info sender = userInfoRepository.findById(senderId).orElse(null);
        User_Info receiver = userInfoRepository.findById(receiverId).orElse(null);

        if (event == null || sender == null || receiver == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Event or users not found"));
        }

        // need to check if sender is an attendee first
        if (!event.getAttendees().contains(sender)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error",
                            "message", "You must be attending the event to invite someone"));
        }

        // if receiver is already attending fixed
        if (event.getAttendees().contains(receiver)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "User already attending"));
        }

        // prevent repeat invites
        if (inviteRepo.findByEventIdAndReceiverId(eventId, receiverId).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "User already invited"));
        }

        EventInvitation invite = new EventInvitation();
        invite.setEvent(event);
        invite.setSender(sender);
        invite.setReceiver(receiver);

        EventInvitation saved = inviteRepo.save(invite);

        notificationsService.notifyEvent(receiver, event,
                sender.getUserName() + " invited you to attend '" + event.getEventName() + "'!"
        );

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Invite sent",
                "inviteId", saved.getId(),
                "senderId", saved.getSender().getId(),
                "senderName", saved.getSender().getUserName(),
                "receiverId", saved.getReceiver().getId(),
                "receiverName", saved.getReceiver().getUserName(),
                "eventId", event.getId(),
                "eventName", event.getEventName()
        ));
    }


    public ResponseEntity<?> acceptInvite(long inviteId) {

        EventInvitation invite = inviteRepo.findById(inviteId).orElse(null);
        if (invite == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status","error","message","Invite not found"));
        }

        // only pending invites should be accepted
        if (!invite.getStatus().equals(PENDING)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status","error","message","Invite already resolved"));
        }

        invite.setStatus(ACCEPTED);
        inviteRepo.save(invite);

        Events event = invite.getEvent();
        User_Info receiver = invite.getReceiver();

        event.getAttendees().add(receiver);
        eventsRepository.save(event);

        notificationsService.notifyEvent(receiver, event,
                invite.getSender().getUserName() + " will attend '" + event.getEventName() + "' with you!");

        notificationsService.notifyEvent(invite.getSender(), event,
                receiver.getUserName() + " accepted your invite to '" + event.getEventName() + "'!"
        );

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Invite accepted. User added to event attendees."
        ));
    }

    public ResponseEntity<?> declineInvite(Long inviteId, int userId) {
        Optional<EventInvitation> inviteOpt = inviteRepo.findById(inviteId);
        if (inviteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Invite not found"));
        }

        EventInvitation invite = inviteOpt.get();
        Events event = invite.getEvent();

        // receiver can decline ONLY!!
        if (invite.getReceiver().getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You are not the recipient"));
        }

        if (!invite.getStatus().equals(PENDING)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Invite already resolved"));
        }

        invite.setStatus(DECLINED);
        inviteRepo.save(invite);


        // notify sender
        notificationsService.notifyEvent(
                invite.getSender(), event,
                invite.getReceiver().getUserName() + " has declined your invite to attend '" + event.getEventName() + "'"
        );

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Invite declined",
                "inviteId", inviteId
        ));
    }

    public ResponseEntity<?> getSentInvites(int userId) {
        List<EventInvitation> invites = inviteRepo.findBySenderId(userId);

        List<Map<String, Object>> list = invites.stream()
                .map(inv -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("inviteId", inv.getId());
                    map.put("eventId", inv.getEvent().getId());
                    map.put("eventName", inv.getEvent().getEventName());
                    map.put("friendId", inv.getReceiver().getId());
                    map.put("friendName", inv.getReceiver().getUserName());
                    map.put("status", inv.getStatus());
                    map.put("sentAt", inv.getSentAt());
                    return map;
                })
                .toList();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "sentInviteCount", list.size(),
                "invites", list
        ));
    }

    public ResponseEntity<?> cancelInvite(Long inviteId, int senderId) {

        Optional<EventInvitation> inviteOpt = inviteRepo.findById(inviteId);
        if (inviteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Invite not found"));
        }

        EventInvitation invite = inviteOpt.get();
        Events event = invite.getEvent();

        // only the sender can cancel the request (when it is pending)
        if (invite.getSender().getId() != senderId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You cannot cancel this invite"));
        }

        if (!invite.getStatus().equals(PENDING)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Only pending invites can be cancelled"));
        }

        inviteRepo.delete(invite);

        // Send notification
        notificationsService.notifyEvent(invite.getReceiver(), event,
                "Your invite to '" + event.getEventName() + "' has been unsent."
        );

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Invite cancelled",
                "inviteId", inviteId
        ));
    }
}

