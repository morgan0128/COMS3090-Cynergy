package User_Info.repository;

//import User_Info.composite.Map_NodeKey;
import User_Info.model.Events;
import User_Info.model.Map_Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Map_NodeRepository extends JpaRepository<Map_Node, Long> {
    List<Long> findAllByAssociatedEventsContaining(Events event);

//    List<Long> streamByMap_node_id();
}
