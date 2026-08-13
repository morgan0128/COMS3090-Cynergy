package User_Info.repository;

import User_Info.model.Event_Chat_Room;
import User_Info.model.Events;
import User_Info.model.Priv_Chat_Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Event_Chat_RoomRepository extends JpaRepository<Event_Chat_Room, Integer> {
    List<Event_Chat_Room> findByEvent(Events event);
}

