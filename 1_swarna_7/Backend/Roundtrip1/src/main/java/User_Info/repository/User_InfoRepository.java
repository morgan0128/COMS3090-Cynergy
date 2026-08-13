package User_Info.repository;

import User_Info.model.User_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface User_InfoRepository extends JpaRepository<User_Info, Integer> {
    Optional<User_Info> findByEmailId(String emailId); //holds all account fields, easy deletion
}
