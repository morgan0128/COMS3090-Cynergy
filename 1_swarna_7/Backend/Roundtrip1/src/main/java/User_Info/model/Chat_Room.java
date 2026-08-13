package User_Info.model;

import User_Info.enumerator.Chat_Type;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "chat_room")
@Inheritance(strategy = InheritanceType.JOINED)
public class Chat_Room {

    Chat_Type type;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatRoomId")
    private Integer chatRoom_Id;

//    @ManyToMany
//    @JoinTable(
//            name = "contains_msg",
//            joinColumns = @JoinColumn(name = "chatRoom_Id"),
//            inverseJoinColumns = @JoinColumn(name = "message_id"))
//    Set<Chat_Message> msgs;

    public Chat_Room(){
        this.type = Chat_Type.PRIVCHAT;
    }

    public Chat_Room(Chat_Type ct){
        this.type = ct;
    }

    public int getChatRoom_id(){
        return this.chatRoom_Id;
    }

}
