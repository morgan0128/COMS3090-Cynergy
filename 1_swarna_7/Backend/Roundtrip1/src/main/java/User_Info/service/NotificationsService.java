package User_Info.service;

import User_Info.model.Chat_Message;
import User_Info.model.Events;
import User_Info.model.Notifications;
import User_Info.model.User_Info;
import User_Info.repository.EventsRepository;
import User_Info.repository.NotificationsRepository;
import User_Info.repository.User_InfoRepository;
import User_Info.websocket.NotificationsSocket;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Set;

@Service
public class NotificationsService {
    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private User_InfoRepository userRepo;

    @Autowired
    private EventsRepository eventsRepository;

    /**
     * Schedules a check every hour for an event that start in an hour
     */
    @Scheduled(fixedRate = 10000) //changes to 10 secs for testing
    public void eventCountdown() {
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timeHourLater = LocalDateTime.now().plusHours(1);

        List<Events> upcomingEvents = eventsRepository.findAll().stream()
                .filter(e -> e.getEventDate() != null && e.getEventTime() != null)
                .map(e -> new Object[] { e, LocalDateTime.of(e.getEventDate(), e.getEventTime()) })
                // 2) Keep events that start in the next hour: [now, now+1h)
                .filter(arr -> {
                    LocalDateTime eventDateTime = (LocalDateTime) arr[1];
                    return !eventDateTime.isBefore(timeNow) && eventDateTime.isBefore(timeHourLater);
                })
                .map(arr -> (Events) arr[0])
                .toList();

        // 3) Notify owners and attendees (guard against null attendees list)
        for (Events event : upcomingEvents) {
            notifyEvent(event.getOwner(), event,
                    "REMINDER: Your event \"" + event.getEventName() + "\" starts in 1 hour!");

            Set<User_Info> attendees = event.getAttendees() != null ? event.getAttendees() : Set.of();
            for (User_Info attendee : attendees) {
                notifyEvent(attendee, event,
                        "REMINDER: \"" + event.getEventName() + "\" starts in 1 hour. Check the chat!");
            }
        }
    }

    public Notifications createNotifications(Notifications n) {
        Notifications saved = notificationsRepository.save(n);
        NotificationsSocket.sendNotification(n.getUser().getId(), n.getMessage());
        return saved;
    }

    public List<Notifications> getUserNotifications(User_Info user) {
        return notificationsRepository.findByUser(user);
    }

    public void markAsRead(int id) {
        Notifications n = notificationsRepository.findById(id).orElseThrow();
        n.setStatus(Notifications.Status.READ);
        notificationsRepository.save(n);

    }

    public void clearAll(User_Info user) {
        List<Notifications> all = notificationsRepository.findByUser(user);
        all.forEach(n -> n.setStatus(Notifications.Status.CLEARED));
        notificationsRepository.saveAll(all);
    }

    //fetch by userId directly from service
    public List<Notifications> findByUserId(int userId) {
        User_Info user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationsRepository.findByUser(user);
    }

    /**
     * Helper method for event notification
     * type is stored as EVENT or CHAT - ease of use for frontend
     * @param user
     * @param event
     * @param message
     */
    public void notifyEvent(User_Info user, Events event, String message) {
        Notifications n = new Notifications();
        n.setUser(user);
        n.setEvent(event);
        n.setType("EVENT");
        n.setMessage(message);
        createNotifications(n);
    }

//    /**
//     * Helper method for chat notifications
//     * type is stored as EVENT or CHAT - ease of user on the frontend
//     * @param user
//     * @param message
//     */
    public void notifyChat(User_Info user, Chat_Message message) {
        if (user.getId() != message.getUser().getId()) {
            Notifications n = new Notifications();
            n.setUser(user);
            n.setChatMessage(message);
            n.setType("CHAT");
            n.setMessage(message.getContent());
            createNotifications(n);
        }
    }

    public void notifyFriendRequest(User_Info receiver, User_Info sender) {
        Notifications n = new Notifications();
        String msg = sender.getUserName() + " sent you a friend request!"; //sender sent the request
        n.setUser(receiver); // receiver
        n.setMessage(msg);
        n.setType("FRIEND_REQUEST_SENT"); //this can be improved with enums and d.setType(Type.FRIEND_REQUEST_SENT), not implemented yet
        createNotifications(n);
    }

    public void notifyFriendAccepted(User_Info user, User_Info friend) {
        Notifications n = new Notifications();
        String msg = friend.getUserName() + " accepted your friend request!";
        n.setUser(user);
        n.setMessage(msg);
        n.setType("FRIEND_REQUEST_ACCEPTED");
        createNotifications(n);
    }

    public void notifyUsersBasedOnInterest(Events event) {

        Set<String> eventTags = event.getTags();
        if (eventTags == null || eventTags.isEmpty())
            return;

        // find all users
        List<User_Info> allUsers = userRepo.findAll();

        for (User_Info user : allUsers) {

            if (user.getProfile() == null) continue;

            Set<String> interests = user.getProfile().getInterests();  // interests is List<String>
            if (interests == null || interests.isEmpty()) continue;

            // checks for at least 1 matching interest
            boolean matches = interests.stream().anyMatch(eventTags::contains);

            if (matches) {
                notifyEvent(
                        user,
                        event,
                        "New Event Recommended: '" + event.getEventName() + "' matches your interests!"
                );
            }
        }
    }

}