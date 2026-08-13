package User_Info.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.apache.catalina.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String eventName;
    private String eventLocation;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String description;
    private boolean sponsoredRequested = false;
    private boolean sponsoredApproved = false;
    private String sponsorName;

    @ElementCollection
    private Set<String> tags = new HashSet<>();

    // relationship with user
    @JsonBackReference(value = "user-events")
    @ManyToOne
    @JoinColumn(name = "owner_id") // key column in events table
    private User_Info owner;

    @OneToMany (mappedBy = "event", cascade = CascadeType.ALL)
    @JsonManagedReference (value = "event-notifications")
    private List<Notifications> notifications = new ArrayList<>();


    // Event attendees for Event attendee notifications
    // user_id will be the foreign key for the table

    @ManyToMany
    @JoinTable(
            name = "event_attendees",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore // switched from JsonManagedReference
    private Set<User_Info> attendees = new HashSet<>();

//    @ManyToMany
//    @JoinTable (name = "event_attendees", joinColumns = @JoinColumn(name = "event_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id"))
//    //@JsonIgnoreProperties("attendingEvents") //ADDED AFTER DEMO 3 DUE TO RECURSIVE ISSUE
//    private Set<User_Info> attendees = new HashSet<>();

    // DO NOT ADD JSONMANAGEDREFS
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setEvent(this);
    }

    public Events() {
    }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getEventLocation() { return eventLocation; }
    public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate (LocalDate eventDate) { this.eventDate = eventDate; }
    public LocalTime getEventTime() { return eventTime; }
    public void setEventTime(LocalTime eventTime) { this.eventTime = eventTime;}
    public User_Info getOwner() { return owner; }
    public void setOwner(User_Info owner) { this.owner = owner; }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id;}
    public boolean isSponsoredApproved() { return sponsoredApproved; }
    public void setSponsorApproved(boolean sponsoredApproved) { this.sponsoredApproved = sponsoredApproved; }
    public void setSponsoredRequested(boolean sponsoredRequested) { this.sponsoredRequested = sponsoredRequested; }
    public boolean isSponsoredRequested() { return sponsoredRequested; }
    public String getSponsorName() { return sponsorName; }
    public void setSponsorName(String sponsorName) { this.sponsorName = sponsorName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<User_Info> getAttendees() { return attendees; }

    public void setAttendees(Set<User_Info> attendees) { this.attendees = attendees; }

    // helpers added for consistency and ease of use
    public void addAttendee(User_Info user) {
        attendees.add(user);
        user.getAttendingEvents().add(this);
    }

    public void removeAttendee(User_Info user) {
        attendees.remove(user);
        user.getAttendingEvents().remove(this);
    }

}
