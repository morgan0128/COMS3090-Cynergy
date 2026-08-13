package User_Info.repository;

import User_Info.model.Review;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByEventId(int eventId);
    List<Review> findByUserId(int userId);

}
