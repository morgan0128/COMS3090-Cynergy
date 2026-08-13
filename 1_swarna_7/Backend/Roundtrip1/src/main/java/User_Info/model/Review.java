package User_Info.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating; // 1-5 stars for the design
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Events event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User_Info user;

    public Review() {}

    public Long getId() { return id; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public void setEvent(Events event) { this.event = event; }
    public Events getEvent() { return event; }
    public void setUser(User_Info user) { this.user = user; }
    public User_Info getUser() { return user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
