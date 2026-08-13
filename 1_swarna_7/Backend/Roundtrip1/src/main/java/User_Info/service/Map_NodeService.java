package User_Info.service;

import User_Info.model.Events;
import User_Info.model.Map_Node;
import User_Info.repository.EventsRepository;
import User_Info.repository.Map_NodeRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Map_NodeService {

    @Autowired
    Map_NodeRepository Map_NodeRepository;

    public Map<String, Object> getMapNodeAsDTO(Long mn_id){
        Map_Node mn = Map_NodeRepository.findById(mn_id).orElseThrow();
        Map<String, Object> mapNodeData = new HashMap<>();
        mapNodeData.put("map_node_id", mn.getMap_node_id());
        mapNodeData.put("color", mn.getColor());
        mapNodeData.put("latitude", mn.getLatitude());
        mapNodeData.put("longitude", mn.getLongitude());
        mapNodeData.put("description", mn.getDescription());
        List<Map<String, Object>> eventsList = new ArrayList<>();
        for (Events e : mn.getAssociatedEvents()){
            // had to move what would better be EventsService logic here
            // as to avoid circular dependency MapNode<->Events
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("id", e.getId());
            eventData.put("eventName", e.getEventName());
            eventData.put("eventLocation", e.getEventLocation());
            eventData.put("eventDate", e.getEventDate());
            eventData.put("eventTime", e.getEventTime());
            eventData.put("attendeeCount", e.getAttendees().size());
            eventsList.add(eventData);
        }
        mapNodeData.put("events", eventsList);
        return mapNodeData;
    }

    // helper
    // returns Optional.isEmpty() if no merge should occur
    public Optional<Map_Node> checkMergePrioEvent(Events e, double lat, double lon){
        Double distance;
        Double minDistance = Double.POSITIVE_INFINITY;
        boolean foundEventNeighbor = false;
        List<Map_Node> mapNodeList = Map_NodeRepository.findAll();
        Optional<Map_Node> neighbor = Optional.empty();
        for (Map_Node mapNode : mapNodeList){
            distance = getDistance(lat, lon, mapNode.getLatitude(), mapNode.getLongitude());
            if (distance < mapNode.getMergeScrutiny()){

                // prioritized
                // then prioritize distance again, but only between neighbors of associateEvents
                if (mapNode.getAssociatedEvents().contains(e)){
                    // Only enters the below a single time, upon discovering that there
                    // exists an associate event in the neighborhod, and whatever we will invariably be returning an
                    // "EventNeighbor" Map_Node (the one of closest dist, in the rare cases where such a comparison arises)
                    if (!foundEventNeighbor){
                        foundEventNeighbor = true;
                        // Reset minDistance
                        minDistance = Double.POSITIVE_INFINITY;
                    }
                    if (distance < minDistance || minDistance.isInfinite()){
                        minDistance = distance;
                        neighbor = Optional.of(mapNode);
                    }

                }
                // Note that in the case of being in the neighborhood (mergeScrutiny distance-wise) of two
                // nodes, we go with the closer. If equal to two or more (practically incredibly rare), we merge with the first found.
                else if (!foundEventNeighbor && (distance < minDistance || minDistance.isInfinite())){
                    minDistance = distance;
                    neighbor = Optional.of(mapNode);
                }
            }
        }
            return neighbor;
        }

    // helper helper
    public Double getDistance(double lat1, double lon1, double lat2, double lon2){
        return Math.sqrt(Math.pow((lat1 - lat2), 2) + Math.pow((lon1 - lon2), 2));
    }

    public List<Map<String, Object>> findAllNodesAssociatedWithEvent(Events e) {
        List<Long> l = Map_NodeRepository.findAllByAssociatedEventsContaining(e);
        List<Map<String, Object>> mnList = new ArrayList<>();
        for (Long id : l) {
            mnList.add(getMapNodeAsDTO(id));
        }
        return mnList;
    }

    public boolean deleteMapNode(long toDeleteId){
        Optional<Map_Node> mno = Map_NodeRepository.findById(toDeleteId);
        if (mno.isEmpty()){
            return false;
        }
        Map_NodeRepository.deleteById(toDeleteId);
        return true;
    }

    public void handleRemoveEventFromAssocNodes(Events e){
        List<Map<String, Object>> assocNodes = findAllNodesAssociatedWithEvent(e);
        for (Map<String, Object> mp : assocNodes) {
            Long id = (Long) mp.get("id");
            Map_Node mn = Map_NodeRepository.findById(id).orElseThrow();
            deleteEventAssociatedMapNode(mn, e);
        }
    }

    public void deleteEventAssociatedMapNode(Map_Node m, Events e){
//        Events foundInAssoc = m.getAssociatedEvents().get(e.getId());
        if (m.removeEventFromAssocReturnEmptied(e)){
            deleteMapNode(m.getMap_node_id());
        }
    }
}


