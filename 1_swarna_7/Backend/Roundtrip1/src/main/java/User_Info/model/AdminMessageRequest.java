package User_Info.model;

import java.util.ArrayList;

// Ran out of time in demo to implement better solution to this.
// A better approach to Send_Message and Chat_Message tables would be ideal.
// This is NOT a table.
public class AdminMessageRequest {

    public ArrayList<Integer> toRooms;

    public String msg;

    public AdminMessageRequest(){

    }

    public AdminMessageRequest(ArrayList<Integer> r, String m){
        this.toRooms = r;
        this.msg = m;
    }

    public String getMsg() {
        return msg;
    }

    public ArrayList<Integer> getToRooms() {
        return toRooms;
    }
}
