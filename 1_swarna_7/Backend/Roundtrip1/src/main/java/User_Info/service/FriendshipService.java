package User_Info.service;

import User_Info.model.Events;
import User_Info.model.Friendship;
import User_Info.model.User_Info;
import User_Info.repository.User_InfoRepository;
import User_Info.repository.FriendshipRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FriendshipService {
    @Autowired
    private FriendshipRepository friendshipRepo;

    @Autowired
    private User_InfoRepository userRepo;

    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    private Chat_RoomService Chat_RoomService;

    @Autowired
    private EventsService eventsService;

    // Send a friend request
    // Note that the person sending the request is the userId here. However, accepting and declining flips this to make the endpoints make sense
    public String sendRequest(int userId, int friendId) {
        User_Info user = userRepo.findById(userId).orElseThrow();

        User_Info friend = userRepo.findById(friendId).orElseThrow();

        // Prevent a self request. Here the sender is the userId and receiver is friendId
        if (userId == friendId) {
            throw new RuntimeException("You cannot friend yourself.");
        }

        // Check if the request already exists (both directions)
        if (friendshipRepo.findByUserAndFriend(user, friend).isPresent()) {
            throw new RuntimeException("You have already sent this request.");
        }
        if (friendshipRepo.findByUserAndFriend(friend, user).isPresent()) {
            throw new RuntimeException("This user has already sent you a request.");
        }

        Friendship relation = new Friendship(user, friend, Friendship.Status.PENDING);
        friendshipRepo.save(relation);

        notificationsService.notifyFriendRequest(friend, user);

        return "Friend request sent!";
    }

    // Accepting, Declining - userId here is the receiver and friendId is the sender
    // Accept friend
    public String acceptRequest(int userId, int friendId) {
        User_Info receiver = userRepo.findById(userId).orElseThrow();
        User_Info sender = userRepo.findById(friendId).orElseThrow();

        Friendship request = friendshipRepo.findByUserAndFriend(sender, receiver)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        request.setStatus(Friendship.Status.ACCEPTED);
        friendshipRepo.save(request);

        notificationsService.notifyFriendAccepted(sender, receiver);
        Chat_RoomService.createPrivRoom(userId, friendId);

        return "Friend request accepted.";
    }

    // Decline a request
    public String declineRequest(int userId, int friendId) {
        User_Info receiver = userRepo.findById(userId).orElse(null);
        User_Info sender = userRepo.findById(friendId).orElse(null);

        if (receiver == null || sender == null) {
            return "User not found.";
        }

        Optional<Friendship> requestOpt = friendshipRepo.findByUserAndFriend(sender, receiver);
        if (requestOpt.isEmpty()) {
            return "No friend request found.";
        }

        friendshipRepo.delete(requestOpt.get());
        return "Friend request declined.";
    }


    // Remove a friend who has been added
    public String removeFriend(int userId, int friendId) {

        User_Info user = userRepo.findById(userId).orElseThrow();
        User_Info friend = userRepo.findById(friendId).orElseThrow();

        friendshipRepo.findByUserAndFriend(user, friend)
                .ifPresent(friendshipRepo::delete);

        friendshipRepo.findByUserAndFriend(friend, user)
                .ifPresent(friendshipRepo::delete);

        return "Friend has been removed.";
    }

    // To GET accepted friends
    public List<Map<String, Object>> getFriends(int userId) {
        Optional<User_Info> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            return List.of(Map.of("status", "error", "message", "User not found"));
        }

        User_Info user = userOpt.get();
        List<Friendship> sent = friendshipRepo.findByUserAndStatus(user, Friendship.Status.ACCEPTED);
        List<Friendship> received = friendshipRepo.findByFriendAndStatus(user, Friendship.Status.ACCEPTED);

        List<Map<String, Object>> result = new ArrayList<>();
        sent.addAll(received);

        for (Friendship f : sent) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", f.getUser().getId());
            map.put("friendId", f.getFriend().getId());
            map.put("status", f.getStatus().toString());
            map.put("created_at", f.getCreatedAt());
            map.put("updated_at", f.getUpdatedAt());

            //fixed to display friend, omitted during a previous edit
            // logic: if a user A sent the request (user_id = A) -> friend is (f.getFriend)
            // if a user B received a request (friend_id = B), and the friend is (f.getUser() = A)
            // ternary handles this logic
            User_Info other = (f.getUser().getId() == userId) ? f.getFriend() : f.getUser();

            //fixed to include minimal info for frontend
            Map<String, Object> friendData = new HashMap<>();
            friendData.put("friendName", other.getUserName());
            friendData.put("friendId", other.getId());

            map.put("friend", friendData);
            result.add(map);
        }
        return result;
    }

    // GET pending requests sent
    public List<Map<String, Object>> getPendingSent(int userId) {
        Optional<User_Info> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            return List.of(Map.of("status", "error", "message", "User not found"));
        }

        User_Info user = userOpt.get();
        List<Friendship> pendingSent = friendshipRepo.findByUserAndStatus(user, Friendship.Status.PENDING);

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Friendship f : pendingSent) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", f.getUser().getId());
            map.put("friendId", f.getFriend().getId());
            map.put("status", f.getStatus().toString());
            map.put("created_at", f.getCreatedAt());
            map.put("updated_at", f.getUpdatedAt());

            // include minimal info about the receiver
            Map<String, Object> friendInfo = new HashMap<>();
            friendInfo.put("id", f.getFriend().getId());
            friendInfo.put("name", f.getFriend().getUserName()); // switched from email to username
            map.put("friend", friendInfo);

            responseList.add(map);
        }

        return responseList;
    }

    // GET all received requests
    public List<Map<String, Object>> getPendingReceived(int userId) {
        Optional<User_Info> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            return List.of(Map.of("status", "error", "message", "User not found"));
        }

        User_Info user = userOpt.get();
        List<Friendship> pendingReceived = friendshipRepo.findByFriendAndStatus(user, Friendship.Status.PENDING);

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Friendship f : pendingReceived) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", f.getUser().getId());
            map.put("friendId", f.getFriend().getId());
            map.put("status", f.getStatus().toString());
            map.put("created_at", f.getCreatedAt());
            map.put("updated_at", f.getUpdatedAt());

            // include minimal info about the sender
            Map<String, Object> senderInfo = new HashMap<>();
            senderInfo.put("id", f.getUser().getId());
            senderInfo.put("name", f.getUser().getUserName()); // switched from email to username
            map.put("sender", senderInfo);

            responseList.add(map);
        }

        return responseList;
    }

    // Morgan
    public record FriendInterestedEventsDto(
            int friendId,
            String friendUsername,
            Map<String, Object> interestedEvents
    ){}
    public record FriendAttendingEventDto(
            int friendId,
            String friendUsername
    ){}

    public List<FriendInterestedEventsDto> getFriendsInterestedEvents(int userId) {
        User_Info user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        List<Friendship> asUser = friendshipRepo.findByUserAndStatus(user, Friendship.Status.ACCEPTED);
        List<Friendship> asFriend = friendshipRepo.findByFriendAndStatus(user, Friendship.Status.ACCEPTED);

        // Merge lists
        List<Friendship> accepted = Stream.concat(asUser.stream(), asFriend.stream()).toList();

        // create Map for a user's unique friends/resolving friend being stored in 1 of 2 fields
        Map<Integer, User_Info> uniqueFriends = new LinkedHashMap<>();
        for (Friendship f : accepted) {
            User_Info other = resolveOtherUser(f, userId);
            uniqueFriends.putIfAbsent(other.getId(), other);
        }

        // Build and return the list of DTOs
        return uniqueFriends.values().stream().map(other -> new FriendInterestedEventsDto(
                other.getId(), other.getUserName(), eventsService.getInterestedEvents(other.getId()))).collect(Collectors.toList());
    }

    public List<FriendAttendingEventDto> getFriendsAttendingEvent(int userId, Events event) {
        User_Info user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (event == null) {
            throw new RuntimeException("Event not provided");
        }

        List<Friendship> asUser = friendshipRepo.findByUserAndStatus(user, Friendship.Status.ACCEPTED);
        List<Friendship> asFriend = friendshipRepo.findByFriendAndStatus(user, Friendship.Status.ACCEPTED);

        List<Friendship> accepted = Stream.concat(asUser.stream(), asFriend.stream()).toList();

        // create Map for a user's unique friends/resolving friend being stored in 1 of 2 fields
        Map<Integer, User_Info> uniqueFriends = new LinkedHashMap<>();
        for (Friendship f : accepted) {
            User_Info other = resolveOtherUser(f, userId);
            if (other != null) {
                uniqueFriends.putIfAbsent(other.getId(), other);
            }
        }

        return getFriendAttendingEventDtos(event, uniqueFriends);
    }

    private static List<FriendAttendingEventDto> getFriendAttendingEventDtos(Events event, Map<Integer, User_Info> uniqueFriends) {
        int eventId = event.getId();

        List<FriendAttendingEventDto> result = new ArrayList<>();
        boolean isAttending;
        for (User_Info friend : uniqueFriends.values()) {
            if (friend.getAttendingEvents() == null) {
                continue;
            }
            isAttending = false;
            for (Events e : friend.getAttendingEvents()) {
                if (e != null && e.getId() == eventId) {
                    isAttending = true;
                    break;
                }
            }
            if (isAttending) {
                result.add(new FriendAttendingEventDto(friend.getId(), friend.getUserName()));
            }
        }
        return result;
    }

    // helper for finding friend (friend may be user or friend)
    private User_Info resolveOtherUser(Friendship f, int userId) {
        if (f.getUser() != null && f.getUser().getId() == userId) {
            return f.getFriend();
        } else {
            return f.getUser();
        }
    }
}
