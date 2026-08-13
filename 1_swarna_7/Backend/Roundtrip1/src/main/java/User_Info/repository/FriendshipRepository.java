package User_Info.repository;

import User_Info.model.Friendship;
import User_Info.model.FriendshipKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import User_Info.model.Friendship;
import User_Info.model.FriendshipKey;
import User_Info.model.User_Info;

public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipKey> {
    Optional<Friendship> findByUserAndFriend(User_Info user, User_Info friend);
    List<Friendship> findByUserAndStatus(User_Info user, Friendship.Status status);
    List<Friendship> findByFriendAndStatus(User_Info friend, Friendship.Status status);

}
