package User_Info.repository;

import User_Info.composite.Chat_MemberKey;
import User_Info.model.Admin;
import User_Info.model.Chat_Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
}
