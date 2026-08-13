package User_Info.model;

import User_Info.composite.Chat_MemberKey;
//import User_Info.composite.Map_NodeKey;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Table(name = "map_nodes")
@Data
public class Map_Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "map_node_id")
    private Long map_node_id;

    // default value blue
    String color = "blue";

    private Double latitude;

    private Double longitude;

    @Lob
    private String description;

//    @JsonManagedReference
    //@ManyToMany(mappedBy = "map_nodes")
    @ManyToMany
    @JoinTable(
            name = "associated_events",
            joinColumns = @JoinColumn(name = "map_node_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private List<Events> associatedEvents;
// Do not use HashMap - this list should be very short
//    private HashMap<Integer, Events> associatedEvents;

//    @Transient
//    @JsonIgnore
//    private HashMap<Events, List<Map_Node>> eventMapper;

    // This is where the default value of mergeScrutiny is stored universally for Map_Nodes
    private final Double mergeScrutiny = 0.00085;

//    @JsonManagedReference
//    @OneToOne(mappedBy = "map_node")
//    private Node associatedNode;


    // standard constructors, getters, and setters
    public Map_Node(){}

    public Map_Node(double lat, double lon, Events e){
        this.latitude = lat;
        this.longitude = lon;
        this.associatedEvents = new ArrayList<Events>();
        this.associatedEvents.add(e);
    }


    public Long getMap_node_id() {
        return map_node_id;
    }

    public String getColor(){
        return this.color;
    }

    public List<Events> getAssociatedEvents(){
        return this.associatedEvents;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getDescription() {
        return description;
    }

    public double getMergeScrutiny() {
        return mergeScrutiny;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setColor(String c){
        this.color = c;
    }

    public void addToAssociatedEvents(Events event){
        this.associatedEvents.add(event);
    }

    public boolean hasColor(){
        return (this.color != null);
    }

    public boolean hasDescription(){
        return (this.description != null);
    }

    public boolean hasLatitude(){
        return (this.latitude != null);
    }

    public boolean hasLongitude(){
        return (this.longitude != null);
    }

    public boolean hasAssociatedEvents() {
        return (!this.associatedEvents.isEmpty());
    }

    public boolean removeEventFromAssocReturnEmptied(Events e){
        this.getAssociatedEvents().remove(e);
        return this.getAssociatedEvents().isEmpty();
    }


}
