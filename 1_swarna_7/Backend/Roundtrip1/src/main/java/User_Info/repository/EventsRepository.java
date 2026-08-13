package User_Info.repository;

import User_Info.model.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Events, Integer>{
    List<Events> findByOwnerId(int ownerId); // GET mapping
}
