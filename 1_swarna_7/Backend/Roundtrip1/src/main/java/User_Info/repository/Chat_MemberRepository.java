package User_Info.repository;

import User_Info.composite.Chat_MemberKey;
import User_Info.model.Chat_Member;
import User_Info.model.Chat_Room;
import User_Info.model.User_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface Chat_MemberRepository extends JpaRepository<Chat_Member, Chat_MemberKey> {
}

