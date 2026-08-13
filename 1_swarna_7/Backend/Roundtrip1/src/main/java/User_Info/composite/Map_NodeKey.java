//package User_Info.composite;
//
//import User_Info.model.Chat_Member;
//import User_Info.model.Events;
//import User_Info.model.Node;
//import jakarta.persistence.*;
//
//import java.io.Serializable;
//import java.util.Objects;
//
//@Embeddable
//public class Map_NodeKey implements Serializable {
//
//    @Column(name = "composite_node_id")
//    private long compNodeId;
//
//    @Column(name = "comosite_events_id")
//    private int compEventsId;
//
//    public Map_NodeKey(){
//
//    }
//
//    public Map_NodeKey(long nId, int eId){
//        this.compNodeId = nId;
//        this.compEventsId = eId;
//    }
//
//    public long getNodeId() {
//        return this.compNodeId;
//    }
//
//    public Integer getEventsId(){
//        return this.compEventsId;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(this.getNodeId(), this.getEventsId());
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        if (object == null || getClass() != object.getClass()) return false;
//        Map_NodeKey that = (Map_NodeKey) object;
//        return compNodeId == that.compNodeId && compEventsId == that.compEventsId;
//    }
//}
