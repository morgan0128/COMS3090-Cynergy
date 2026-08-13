package User_Info.repository;

import User_Info.model.Admin;
import User_Info.model.Admin_Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Admin_IssueRepository extends JpaRepository<Admin_Issue, Long> {
    List<Admin_Issue> findAllByResolved(boolean resolved);
}
