package User_Info.repository;

import User_Info.model.Events;
import User_Info.model.Priv_Chat_Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface Priv_Chat_RoomRepository extends JpaRepository<Priv_Chat_Room, Integer> {
    List<Priv_Chat_Room> findDistinctByFriendOneIdEquals(int userId);

    List<Priv_Chat_Room> findDistinctByFriendTwoIdEquals(int userId);
}
