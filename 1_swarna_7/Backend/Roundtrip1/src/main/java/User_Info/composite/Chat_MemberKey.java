package User_Info.composite;

import User_Info.model.Chat_Member;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Chat_MemberKey implements Serializable {

    @Column(name = "user_Id")
    private Integer userId;

    @Column(name = "chat_room_Id")
    private Integer chatRoomId;

    public Chat_MemberKey(){

    }

    public Chat_MemberKey(int uId, int crId){
        this.userId = uId;
        this.chatRoomId = crId;
    }

    public int getUserId() {
        return this.userId;
    }

    public Integer getChatRoomId(){
        return this.chatRoomId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getChatRoomId());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Chat_MemberKey that = (Chat_MemberKey) object;
        return getUserId() == that.getUserId() && getChatRoomId() == that.getChatRoomId();
    }
}
