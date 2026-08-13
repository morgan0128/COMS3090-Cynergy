package User_Info.controller;

import User_Info.model.Events;
import User_Info.model.Map_Node;
import User_Info.repository.EventsRepository;
import User_Info.repository.Map_NodeRepository;
import User_Info.service.Map_NodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
public class Map_NodeController {

    @Autowired
    EventsRepository EventsRepository;

    @Autowired
    Map_NodeRepository Map_NodeRepository;

    @Autowired
    Map_NodeService Map_NodeService;

    @Operation(summary = "POST for map node: Value of mergescrutiny in Map_Node is used to decide the following:" +
            "if POST request is close (by mergescrutiny) to other mapnode(s): " +
            "if none of the close mapnode(s) contain event of POST request mapnode event, then merge this request event into the closed map node - no new node created." +
            "...if > 0 of the close mapnode(s) contain event of POST request mapnode event, operation cancelled." +
            "If no close map nodes, POST the new request.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conditional on the above: merged or completed POST",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Operation cancelled: Requested to POST a mapnode which neighbors too closely a mapnode which contains this event",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Event not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    })
    @PostMapping("api/map/node/create/{eventId}/{latitude}/{longitude}")
    public ResponseEntity<?> createMapNodeWithCheckMergeOnScrutiniesPrioEventOverDist(@PathVariable int eventId, @PathVariable Double latitude, @PathVariable Double longitude, @RequestBody(required = false) String desc) {
        Optional<Events> eventO = EventsRepository.findById(eventId);
        if (eventO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found associated with the provided eventId");
        }
        // No need to check for Map_Node associated with already existing - this is allowed
        // However on merge we will prioritize merging to existing Map_Node associated with event if scrutiny accepts
        Events event = eventO.get();
        Optional<Map_Node> mergeMNO = Map_NodeService.checkMergePrioEvent(event, latitude, longitude);

        // WE WILL NOT BE CREATING A NEW MAP_NODE
        if (mergeMNO.isPresent()) {
            Map_Node mergeMN = mergeMNO.get();
            // If we found a Map_Node associated with the same event already in the (distance) neighborhood
            if (mergeMN.getAssociatedEvents().contains(event)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot create another Map_Node of event which neighbors (by distance) an existing Map_Node of said event");
            } else {
                mergeMN.addToAssociatedEvents(event);
                Map_Node c = Map_NodeRepository.save(mergeMN);
                return ResponseEntity.ok("No new Map_Node created: instead, your provided event is stored in existing Map_Nde " + Map_NodeService.getMapNodeAsDTO(c.getMap_node_id()) + "," +
                        "which is closely neighboring (in terms of distance) the location that you provided.");
            }
        } else {
            Map_Node newMN = new Map_Node(latitude, longitude, event);
            if (desc !=  null){
                newMN.setDescription(desc);
            }
            Map_Node c = Map_NodeRepository.save(newMN);
            URI location = URI.create("/api/map/node/" + c.getMap_node_id());
            return ResponseEntity.ok(Map_NodeService.getMapNodeAsDTO(c.getMap_node_id()));
        }
    }

    @Operation(summary = "Retrieve all mapnodes")
    @GetMapping("api/map/node/all")
    public List<Map<String, Object>> getAllMapNodes(){
        List<Map_Node> mnObjList = Map_NodeRepository.findAll();
        List<Long> mnIdList = mnObjList.stream().map(Map_Node::getMap_node_id).toList();

        List<Map<String, Object>> mnList = new ArrayList<>();
        for (Long id : mnIdList){
            mnList.add(Map_NodeService.getMapNodeAsDTO(id));
        }
        return mnList;
    }

    @Operation(summary = "Retrieve all events associated with provided (by mapnodeid) map node")
    @GetMapping("api/map/node/all/{mapNodeId}")
    public List<Map<String, Object>> getAllEventsAssociatedWithThisMapNode(@PathVariable Long mapNodeId){
        Optional<Map_Node> mapNodeO = Map_NodeRepository.findById(mapNodeId);
        if (mapNodeO.isEmpty()){
            throw new RuntimeException();
        }
        Map_Node mapNode = mapNodeO.get();
        List<Map<String, Object>> eventsList = new ArrayList<>();
        for (Events e : mapNode.getAssociatedEvents()){
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("id", e.getId());
            eventData.put("eventName", e.getEventName());
            eventData.put("eventLocation", e.getEventLocation());
            eventData.put("eventDate", e.getEventDate());
            eventData.put("eventTime", e.getEventTime());
            eventData.put("attendeeCount", e.getAttendees().size());
            eventsList.add(eventData);
        }
        return eventsList;

    }

    @Operation(summary = "Retrieve all mapnodes containing (by eventId) event")
    @GetMapping("/api/map/node/{eventId}")
    public List<Map<String, Object>> findMapNodesByAssociatedEvent(@PathVariable Integer eventId){
        Optional<Events> eventAssociatedO =  EventsRepository.findById(eventId);
        if (eventAssociatedO.isEmpty()){
            throw new RuntimeException();
        }
        Events eventAssociated = eventAssociatedO.get();

        return Map_NodeService.findAllNodesAssociatedWithEvent(eventAssociated);
    }

    @Operation(summary = "Edit the description of mapnode - JSON RequestBody of only a string for description - any quotation marks will be considered a part of the string.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Mapnode not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    })
    @PutMapping("api/map/node/edit/description/{mapNodeId}")
    public ResponseEntity<?> editMapNode(@PathVariable Long mapNodeId, @RequestBody(required = false) String desc){
        if (Map_NodeRepository.findById(mapNodeId).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Map_Node not found. Please include the Map_Node's Id (Long) in the RequestBody.");
        }
        Map_Node foundMN = Map_NodeRepository.findById(mapNodeId).get();
        foundMN.setDescription(desc);
        Map_Node saved = Map_NodeRepository.save(foundMN);
        return ResponseEntity.ok(Map_NodeService.getMapNodeAsDTO(saved.getMap_node_id()));
    }

    @Operation(summary = "Delete a mapnode - events themselves will not be affected")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mapnode deleted",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Deletion cancelled: mapnode not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("api/map/node/delete/{mapNodeId}")
    public ResponseEntity<?> deleteMapNode(@PathVariable Long mapNodeId){
        boolean deleted = Map_NodeService.deleteMapNode(mapNodeId);
        if (!deleted){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Map_Node not found.");
        } else {
            return ResponseEntity.ok("Map_Node " + mapNodeId + " has been deleted. Note that the event(s) found on this node have not been deleted.");

        }
    }

    //helper
//    public Optional<Map_Node> nodeCheckMaybeCombine(Node n){
//        // Modify this to adjust scrutiny of node distance for check if combine
//        // Should not be modified without resetting all existing Map_Nodes
//        double comparisonScrutiny = 0.001;
//
//        List<Map_Node> mapNodeList = Map_NodeRepository.findAll();
//        for (Map_Node node : mapNodeList){
//            Optional<Node> compareO = NodeRepository.findById(node.getIdOfNode());
//            // although compareO should NOT be null
//            if (compareO.isEmpty()){
//                throw new RuntimeException();
//            }
//            Node compare = compareO.get();
//            if (n.getLatitude() - compare.getLatitude() < comparisonScrutiny || n.getLatitude() - compare.getLatitude() < (-1 * comparisonScrutiny)){
//                if (n.getLongitude() - compare.getLongitude() < comparisonScrutiny || n.getLongitude() - compare.getLongitude() < (-1 * comparisonScrutiny)){
//                    return Optional.of(node);
//                }
//            }
//        }
//        return Optional.empty();
//    }


}
