package User_Info.repository;

import User_Info.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.util.Optional;
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    //Profile findByEmailId(String name);
    //Optional<Profile>
    Profile findByProfileId(int profileId);

}
