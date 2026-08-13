package User_Info.repository;

import User_Info.model.EventInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Event_InvitationRepository extends JpaRepository<EventInvitation, Long> {
    List<EventInvitation> findBySenderId(int senderId);

    List<EventInvitation> findByReceiverId(int receiverId);

    Optional<EventInvitation> findByEventIdAndReceiverId(int eventId, int receiverId);
}
