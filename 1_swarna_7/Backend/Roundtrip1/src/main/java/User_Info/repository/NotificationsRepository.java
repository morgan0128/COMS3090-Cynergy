package User_Info.repository;

import User_Info.model.User_Info;
import User_Info.model.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {
    List<Notifications> findByUser(User_Info user);
    List<Notifications> findByUserAndStatus(User_Info user, Notifications.Status status);
 }
