package User_Info.model;

import User_Info.composite.Chat_MemberKey;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_members")
@Data
public class Chat_Member {

    @EmbeddedId
    Chat_MemberKey cmk_id;


//    @ManyToOne
//    @MapsId("userId")
//    @JoinColumn(name = "user_Id")
//    User_Info user;
//
//    @ManyToOne
//    @MapsId("chatRoomId")
//    @JoinColumn(name = "chatRoom_Id")
//    Chat_Room chatRoom;

//    @ManyToMany(mappedBy="id")
//    Set<User_Info> members;

    // standard constructors, getters, and setters
    public Chat_Member(){}


    public Chat_Member(Chat_MemberKey cmk){
        this.cmk_id = cmk;
    }
//    public Chat_Member(User_Info u, Chat_Room c){
//        this.user = u;
//        this.chatRoom = c;
//        this.cmk_id = new Chat_MemberKey();
//    }

    public Integer getChatRoomId(){
        return this.cmk_id.getChatRoomId();
    }

    public Integer getUserId(){
        return this.cmk_id.getUserId();
    }

    public Chat_MemberKey getCMK(){
        return this.cmk_id;
    }

}
