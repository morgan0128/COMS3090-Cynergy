package User_Info.repository;

import User_Info.model.Chat_Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Chat_RoomRepository  extends JpaRepository<Chat_Room, Integer> {
}
