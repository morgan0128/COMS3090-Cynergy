package User_Info.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chat_messages")
@Data
public class Chat_Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long message_id;

    @JsonBackReference
    @ManyToOne
//    @JoinColumn(name = "sent_by_user", nullable = false, foreignKey = @ForeignKey(name="id"))
    @JoinColumn(name = "sent_by_user", nullable = false)
    private User_Info user;

    // Added to correct model
    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "message-notifications")
    private List<Notifications> notifications = new ArrayList<>();

//    @ManyToMany(mappedBy="msgs")
//    Set<Chat_Room> toChatRooms;

    @Column
    private String userName;

    @Lob
    private String message_string;

//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "date_time")
//    private Date time = new Date();


    public Chat_Message() {};

    public Chat_Message(String userName, String mStr, User_Info u) {
        this.userName = userName;
        this.message_string = mStr;
        this.user = u;
    }

    public Long getId() {
        return message_id;
    }

    public void setId(Long id) {
        this.message_id = id;
    }
    //placeholder
    public String getContent() {
        return message_string;
    }



    public String getUserName() {
        return userName;
    }

    public User_Info getUser() {
        return user;
    }

    public void setUser(User_Info user) {
        this.user = user;
    }

//    public void setUserName(String userName) {
//        this.userName = userName;
//    }

//    public String getMessageString() {
//        return message_string;
//    }

//    public void setMessageString(String msgStr) {
//        this.message_string = msgStr;
//    }

//    public Date getTime() {
//        return time;
//    }

//    public void setTime(Date time) {
//        this.time = time;
//    }



}