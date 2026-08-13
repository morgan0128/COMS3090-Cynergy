package User_Info.model;

import User_Info.composite.Send_MessageKey;
import jakarta.persistence.*;

@Entity
public class Send_Message {

    @EmbeddedId
    Send_MessageKey smkId;

//    @ManyToOne
//    @MapsId("messageId")
//    @JoinColumn(name = "message_id")
//    Chat_Message chatMessage;

//    @ManyToOne
//    @MapsId("chatRoomId")
//    @JoinColumn(name = "chatRoom_id")
//    Chat_Room chatRoom;

//    @Lob
//    private String message_string;

//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "date_time")
//    private Date time = new Date();

    // standard constructors, getters, and setters
    public Send_Message(){}

    public Send_Message(Send_MessageKey smk){
        this.smkId = smk;
    }

    public Integer getChatRoomId(){
        return this.smkId.getChatRoomId();
    }

    public Long getMessageId(){
        return this.smkId.getMessageId();
    }

    public Send_MessageKey getSMK(){
        return this.smkId;
    }

}
