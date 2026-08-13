package User_Info.repository;

import User_Info.composite.Send_MessageKey;
import User_Info.model.Chat_Message;
import User_Info.model.Send_Message;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Send_MessageRepository extends JpaRepository<Send_Message, Send_MessageKey> {
    List<Send_Message> findAllBySmkIdChatRoomId(Integer chatRoomId);
}
