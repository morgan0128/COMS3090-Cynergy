package User_Info.model;

//import org.springframework.boot.*;
//import com.h2database.*;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import java.util.List;

//import jakarta.persistence.OneToOne;
//import jakarta.persistence.Column; // To set E-mail as unique class if we want


@Entity
public class User_Info {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "user_info", cascade = CascadeType.ALL)
    //@JsonManagedReference
    @JsonIgnore
    private Profile profile;

    @OneToOne(mappedBy = "user_info", cascade = CascadeType.ALL)
    private Admin admin;

    // for user who wrote the review
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    /**
     * Uncomment below to make email column unique.
     * Might have to reset db to apply changes.
     */
//    @Column(unique = true, nullable = false)
//    @Email
    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    private String emailId;

    @JsonManagedReference(value = "user-events")
    @OneToMany (mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Events> events;

    @ManyToMany(mappedBy = "attendees")
    @JsonIgnore
    private Set<Events> attendingEvents = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private Set<Chat_Message> messages;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notifications> notifications = new ArrayList<>();


    @Setter
    private String userName;
    private String phoneNum;
    private String userPassword;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

//    public Admin getAdmin() {
//        return admin;
//    }

    public void setAdmin(Admin a) {
        this.admin = a;
    }


    public User_Info(String e, String u, String p){
        this.emailId = e;
        this.userName = u;
        this.userPassword = p;
    }

    public User_Info() {
    }

    // easy to save new notifications
    public void addNotification(Notifications n) {
        notifications.add(n);
        n.setUser(this);
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void unassignProfile(){
        this.profile = null;
    }

    public void unassignAdmin(){
        this.admin = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public boolean hasUserName() {
        return userName != null;
    }

    public boolean hasUserPassword() {
        return userPassword != null;
    }

    public Set<Events> getAttendingEvents() {
        return attendingEvents;
    }
    public void setAttendingEvents(Set<Events> attendingEvents) {
        this.attendingEvents = attendingEvents;
    }

}