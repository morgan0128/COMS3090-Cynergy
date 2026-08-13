package User_Info.controller;

import User_Info.model.Review;
import User_Info.model.Events;

import User_Info.model.User_Info;
import User_Info.repository.EventsRepository;
import User_Info.repository.ReviewRepository;
import User_Info.repository.User_InfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    EventsRepository eventsRepo;

    @Autowired
    User_InfoRepository userRepo;

    // For user to add a review
    @PostMapping("/event/{eventId}/user/{userId}")
    public ResponseEntity<?> createReview(@PathVariable int eventId, @PathVariable int userId, @RequestBody Review data) {
        Events event = eventsRepo.findById(eventId).orElse(null);
        User_Info user = userRepo.findById(userId).orElse(null);

        if (event == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Event or User not found"));
        }

        // to check if user attended that event
        if (!event.getAttendees().contains(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "User did not attend this event"));
        }

        Review review = new Review();
        review.setRating(data.getRating());
        review.setComment(data.getComment());
        review.setEvent(event);
        review.setUser(user);

        event.getReviews().add(review);
        eventsRepo.save(event);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Review submitted"
        ));
    }

    @GetMapping("/event/{eventId}/reviews")
    public ResponseEntity<Map<String, Object>> getEventReviews(@PathVariable Integer eventId) { // changed from int during testing
        Events event = eventsRepo.findById(eventId).orElse(null);

        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "error",
                            "message", "Event not found"
                    ));
        }

        List<Map<String, Object>> reviewList = event.getReviews()
                .stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", r.getId());
                    map.put("rating", r.getRating());
                    map.put("comment", r.getComment());
                    map.put("createdAt", r.getCreatedAt());
                    map.put("userId", r.getUser().getId());
                    map.put("userName", r.getUser().getUserName());
                    return map;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("eventId", eventId);
        response.put("reviewCount", reviewList.size());
        response.put("reviews", reviewList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReviews(@PathVariable int userId) {
        User_Info user = userRepo.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
        }

        List<Review> reviews = reviewRepo.findByUserId(userId);

        List<Map<String, Object>> reviewList = reviews.stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("reviewId", r.getId());
                    map.put("rating", r.getRating());
                    map.put("comment", r.getComment());
                    map.put("createdAt", r.getCreatedAt());
                    map.put("eventId", r.getEvent().getId());
                    map.put("eventName", r.getEvent().getEventName());
                    return map;
                })
                .toList();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "userId", userId,
                "reviewCount", reviewList.size(),
                "reviews", reviewList
        ));
    }

    @DeleteMapping("/{eventId}/reviews/{reviewId}/{userId}")
    public ResponseEntity<?> deleteReview(@PathVariable int eventId, @PathVariable long reviewId, @PathVariable int userId) {
        Events event = eventsRepo.findById(eventId).orElse(null);
        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Event not found"));
        }

        Review target = event.getReviews().stream()
                .filter(r -> r.getId() == reviewId)
                .findFirst()
                .orElse(null);

        if (target == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Review not found"));
        }

        // so a user can only delete their own review
        if (target.getUser().getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Not authorized to delete this review"));
        }

        event.getReviews().remove(target);
        eventsRepo.save(event);

        return ResponseEntity.ok(
                Map.of("status", "success", "message", "Review deleted")
        );
    }
}
