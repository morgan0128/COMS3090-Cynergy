package User_Info.repository;

import User_Info.model.Chat_Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Chat_MessageRepository extends JpaRepository<Chat_Message, Long>
{
}
