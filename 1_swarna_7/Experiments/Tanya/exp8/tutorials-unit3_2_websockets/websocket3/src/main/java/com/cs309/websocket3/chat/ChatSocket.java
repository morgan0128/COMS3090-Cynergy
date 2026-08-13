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

@Controller
@ServerEndpoint(value = "/chat/{interest}/{username}")  // chat based on interested event
public class ChatSocket {

  // cannot autowire static directly (instead we do it by the below
  // method
	private static MessageRepository msgRepo; 

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

	// Store all socket session and their corresponding username and interests.
	private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
	private static Map<String, Session> usernameSessionMap = new Hashtable<>();
	private static Map<Session, String> sessionInterestMap = new Hashtable<>();

	private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username, @PathParam("interest") String interest)
      throws IOException {

		logger.info("User " + username + " joined interest group: " + interest);

    // store connecting user information
		sessionUsernameMap.put(session, username);
		usernameSessionMap.put(username, session);
		sessionInterestMap.put(session, interest);

		//Send interest specific chat history to the newly connected user
		sendMessageToUser(username, getChatHistory(interest));
		
    // broadcast that new user joined
		String message = "User:" + username + " has joined the " + interest + " group chat";
		broadcastToInterestGroup(interest, message);
	}


	@OnMessage
	public void onMessage(Session session, String message) throws IOException {


		String username = sessionUsernameMap.get(session);
		String interest = sessionInterestMap.get(session);

		// Handle new messages
		logger.info("Message from " + username + " in group " + interest + ": " + message);

    // Direct message to a user using the format "@username <message>"
		if (message.startsWith("@")) {
			String destUsername = message.split(" ")[0].substring(1); 

      // send the message to the sender and receiver
			sendMessageToUser(destUsername, "[DM] " + username + ": " + message);
			sendMessageToUser(username, "[DM] " + destUsername + ": " + message); //username instead of destUsername before, check
		} 
    else { // broadcast
			broadcastToInterestGroup(interest, username + ": " + message);
		}

		// Saving chat history to repository
		msgRepo.save(new Message(username, message, interest));
	}


	@OnClose
	public void onClose(Session session) throws IOException {

    // remove the user connection information
		String username = sessionUsernameMap.get(session);
		String interest = sessionInterestMap.get(session);

		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(username);
		sessionInterestMap.remove(session);

    // broadcast that the user disconnected
		String message = username + " left the " + interest + " group.";
		broadcastToInterestGroup(interest, message);
	}


	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
		logger.info("Entered into Error");
		throwable.printStackTrace();
	}

	private void sendMessageToUser(String username, String message) {
		try {
			Session session = usernameSessionMap.get(username);
			if (session != null)
				session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			logger.error("Exception: " + e.getMessage());
		}
	}

	private void broadcastToInterestGroup(String interest, String message) {
		sessionInterestMap.forEach((session, userInterest) -> {
			if (userInterest.equals(interest)) {
				try {
					session.getBasicRemote().sendText(message);
				} catch (IOException e) {
					logger.error("Exception: " + e.getMessage());
				}
			}
		});
	}
	

  // Gets the Chat history from the repository based on interest
	private String getChatHistory(String interest) {
		List<Message> messages = msgRepo.findByInterest(interest);
    
    // convert the list to a string
		StringBuilder sb = new StringBuilder();
		if(messages != null && !messages.isEmpty()) {
			for (Message message : messages) {
				sb.append(message.getUserName() + ": " + message.getContent() + "\n");
			}
		}
		return sb.toString();
	}

}
