package User_Info.model;

//Composite key implementation

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FriendshipKey implements Serializable {
    private int userId;
    private int friendId;

    public FriendshipKey() {}

    public FriendshipKey(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getFriendId() { return friendId; }
    public void setFriendId(int friendId) { this.friendId = friendId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipKey that = (FriendshipKey) o;
        return userId == that.userId &&
                friendId == that.friendId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, friendId);
    }
}
