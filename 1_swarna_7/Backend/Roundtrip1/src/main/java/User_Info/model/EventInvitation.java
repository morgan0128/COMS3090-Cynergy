package User_Info.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class EventInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user sending invite
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User_Info sender;

    // user receiving invite
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User_Info receiver;

    // event being invited to
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Events event;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING,
        ACCEPTED,
        DECLINED
    }
    private LocalDateTime sentAt = LocalDateTime.now();

    public EventInvitation() {}

    public Long getId() { return id; }
    public User_Info getSender() { return sender; }
    public User_Info getReceiver() { return receiver; }
    public Events getEvent() { return event; }
    public Status getStatus() { return status; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSender(User_Info sender) { this.sender = sender; }
    public void setReceiver(User_Info receiver) { this.receiver = receiver; }
    public void setEvent(Events event) { this.event = event; }
    public void setStatus(Status status) { this.status = status; }
}

