package com.cs309.websocket3.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, String>{
    ChatUser findByUsername(String username);
}
