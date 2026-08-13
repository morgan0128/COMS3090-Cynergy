package User_Info.model;

import User_Info.enumerator.Chat_Type;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
public class Event_Chat_Room extends Chat_Room {

    @OneToOne
    @PrimaryKeyJoinColumn
    private Events event;

    public Event_Chat_Room(){

    }

    public Event_Chat_Room(Events e){
        super(Chat_Type.EVENTCHAT);
        this.event = e;
    }
}
