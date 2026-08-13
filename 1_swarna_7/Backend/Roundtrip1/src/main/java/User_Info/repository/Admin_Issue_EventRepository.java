package User_Info.repository;

import User_Info.model.Admin_Issue;
import User_Info.model.Admin_Issue_Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Admin_Issue_EventRepository extends JpaRepository<Admin_Issue_Event, Long> {
}
