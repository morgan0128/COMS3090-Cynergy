package User_Info.model;

import User_Info.enumerator.Chat_Type;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
public class Priv_Chat_Room extends Chat_Room {


    private Integer friendOneId;
    private Integer friendTwoId;

    public Priv_Chat_Room() {

    }

    public Priv_Chat_Room(int friendOneId, int friendTwoId){
        super(Chat_Type.PRIVCHAT);
        this.friendOneId = friendOneId;
        this.friendTwoId = friendTwoId;
    }

    public int getFriend1Id(){
        return this.friendOneId;
    }

    public int getFriend2Id(){
        return this.friendTwoId;
    }


}
