package User_Info.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notiId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User_Info user; // store who receives notifications

    @Column(nullable = false)
    private String type;

    private String message;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonBackReference(value = "event-notifications")
    private Events event;

    @ManyToOne
    @JoinColumn(name = "chat_message_id")
    @JsonBackReference(value = "message-notifications")
    private Chat_Message chatMessage;

    @Enumerated(EnumType.STRING)
    private Status status = Status.UNREAD;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum Status {
        UNREAD, READ, CLEARED
    }

    public Notifications() {}

    // getters and setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public User_Info getUser() {return user;}
    public void setUser(User_Info user){ this.user = user;}

    public Events getEvent() { return event; }
    public void setEvent(Events event) { this.event = event; }

    public Chat_Message getChatMessage() { return chatMessage; }
    public void setChatMessage(Chat_Message chatMessage) { this.chatMessage = chatMessage; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public int getNotiId() {
        return notiId;
    }
}