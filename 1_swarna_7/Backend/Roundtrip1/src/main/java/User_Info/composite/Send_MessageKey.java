package User_Info.composite;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Send_MessageKey implements Serializable {

    @Column(name = "message_Id")
    private Long messageId;

    @Column(name = "chat_room_Id")
    private Integer chatRoomId;

    public Send_MessageKey(){

    }

    public Send_MessageKey(long m, int c){
        this.messageId = m;
        this.chatRoomId = c;
    }

    public long getMessageId(){
        return this.messageId;
    }

    public int getChatRoomId(){
        return this.chatRoomId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessageId(), getChatRoomId());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Send_MessageKey that = (Send_MessageKey) object;
        return getMessageId() == that.getMessageId() && getChatRoomId() == that.getChatRoomId();
    }
}
