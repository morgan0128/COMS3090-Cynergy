package User_Info.model;

import jakarta.persistence.*;
import jakarta.transaction.Status;
import org.apache.catalina.User;

import java.io.ObjectInputFilter;
import java.time.LocalDateTime;

@Entity
@Table(name = "friendship")
public class Friendship {

    @EmbeddedId
    private FriendshipKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User_Info user;

    @ManyToOne
    @MapsId("friendId")
    @JoinColumn(name = "friend_id")
    private User_Info friend;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING,
        ACCEPTED,
        DECLINED
    }


    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Friendship() {}

    public Friendship(User_Info user, User_Info friend, Status status) {
        this.user = user;
        this.friend = friend;
        this.id = new FriendshipKey(user.getId(), friend.getId());
        this.status = status;
    }

    public FriendshipKey getId() { return id; }
    public void setId(FriendshipKey id) { this.id = id; }

    public User_Info getUser() { return user; }
    public void setUser(User_Info user) { this.user = user; }

    public User_Info getFriend() { return friend; }
    public void setFriend(User_Info friend) { this.friend = friend; }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    // For future implementation
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
