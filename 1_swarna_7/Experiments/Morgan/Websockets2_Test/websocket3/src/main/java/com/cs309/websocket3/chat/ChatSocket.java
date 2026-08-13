package com.cs309.websocket3.chat;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/chat/{username}/{password}")  // this is Websocket url
public class ChatSocket {

  // cannot autowire static directly (instead we do it by the below
  // method
	private static MessageRepository msgRepo;

    private static ChatUserRepository chtRepo;

    /*
   * Grabs the MessageRepository singleton from the Spring Application
   * Context.  This works because of the @Controller annotation on this
   * class and because the variable is declared as static.
   * There are other ways to set this. However, this approach is
   * easiest.
	 */
	@Autowired
	public void setMessageRepository(MessageRepository repo) {
		msgRepo = repo;  // we are setting the static variable
	}

    @Autowired
    public void setChatUserRepository(ChatUserRepository repo) {
        chtRepo = repo;
    }

	// Store all socket session and their corresponding username.
	private static Map<Session, ChatUser> sessionChatUserMap = new Hashtable<>();
	private static Map<String, Session> ChatUserSessionMap = new Hashtable<>();

	private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username, @PathParam("password") String password)
      throws IOException {

		logger.info("Entered into Open");

        ChatUser cu = chtRepo.findByUsername(username);
        if (cu != null) {
            if (cu.getPassword().equals(password)){
                // store connecting user information
                sessionChatUserMap.put(session, cu);
                ChatUserSessionMap.put(username, session);

                //Send chat history to the newly connected user
                sendMessageToPArticularUser(username, getChatHistory());

                // broadcast that new user joined
                String message = "User:" + cu.getUsername() + " has Joined the Chat";
                broadcast(message);
            } else {
                session.getBasicRemote().sendText("Incorrect login info");
                session.close();
            }
        } else {
            ChatUser cUser = new ChatUser(username, password);
            chtRepo.save(cUser);
            // store connecting user information
            sessionChatUserMap.put(session, cUser);
            ChatUserSessionMap.put(username, session);

            //Send chat history to the newly connected user
            sendMessageToPArticularUser(username, getChatHistory());

            // broadcast that new user joined
            String message = "New User:" + cUser.getUsername() + " has Joined the Chat";
            broadcast(message);


        }
	}


	@OnMessage
	public void onMessage(Session session, String message) throws IOException {

		// Handle new messages
		logger.info("Entered into Message: Got Message:" + message);
		String username = sessionChatUserMap.get(session).getUsername();

    // Direct message to a user using the format "@username <message>"
		if (message.startsWith("@")) {
			String destUsername = message.split(" ")[0].substring(1); 

      // send the message to the sender and receiver
			sendMessageToPArticularUser(destUsername, "[DM] " + username + ": " + message);
			sendMessageToPArticularUser(username, "[DM] " + username + ": " + message);

		} 
    else { // broadcast
			broadcast(username + ": " + message);
		}

		// Saving chat history to repository
		msgRepo.save(new Message(username, message));
	}


	@OnClose
	public void onClose(Session session) throws IOException {
		logger.info("Entered into Close");

    // remove the user connection information
		String username = sessionChatUserMap.get(session).getUsername();
		sessionChatUserMap.remove(session);
		ChatUserSessionMap.remove(username);

    // broadcase that the user disconnected
		String message = username + " disconnected";
		broadcast(message);
	}


	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
		logger.info("Entered into Error");
		throwable.printStackTrace();
	}


	private void sendMessageToPArticularUser(String username, String message) {
		try {
			ChatUserSessionMap.get(username).getBasicRemote().sendText(message);
		} 
    catch (IOException e) {
			logger.info("Exception: " + e.getMessage().toString());
			e.printStackTrace();
		}
	}


	private void broadcast(String message) {
		sessionChatUserMap.forEach((session, username) -> {
			try {
				session.getBasicRemote().sendText(message);
			}
      catch (IOException e) {
				logger.info("Exception: " + e.getMessage().toString());
				e.printStackTrace();
			}

		});

	}
	

  // Gets the Chat history from the repository
	private String getChatHistory() {
		List<Message> messages = msgRepo.findAll();
    
    // convert the list to a string
		StringBuilder sb = new StringBuilder();
		if(messages != null && messages.size() != 0) {
			for (Message message : messages) {
				sb.append(message.getUserName() + ": " + message.getContent() + "\n");
			}
		}
		return sb.toString();
	}

} // end of Class
