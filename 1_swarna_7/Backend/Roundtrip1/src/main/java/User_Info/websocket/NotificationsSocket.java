package User_Info.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@ServerEndpoint(value = "/ws/notifications/{userId}")
public class NotificationsSocket {

    //stores active ws sessions key-ed by userId
    private static final ConcurrentHashMap<Integer, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen (Session session, @PathParam("userId") int userId) {

        // Concurrent hashmap
        Session existingSession = sessions.put(userId, session);
        if (existingSession != null && existingSession.isOpen()) {
            try {
                existingSession.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Previous session closed for user."));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // sessions.put(userId, session);
        System.out.println("NotificationsSocket: User " + userId + " connected.");
        try {
            session.getBasicRemote().sendText("Connection for notifications successful.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose (Session session, @PathParam("userId") int userId) {
        // want to remove if the same session is still the one mapped
        sessions.computeIfPresent(userId, (id, existing) -> {
            if (existing == session) {
                try {
                    if (existing.isOpen()) {
                        existing.close();
                    }
                } catch (IOException e) {
                    System.err.println("Error closing session for user " + userId + ": " + e.getMessage());
                }

                System.out.println("NotificationsSocket: User " + userId + " disconnected.");
                return null; // remove from mapped
            }
            return existing; //keep the new session
        });
    }

    // client-side for bidirectionality
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") int userId) {
        System.out.println("From user: " + userId + ": " + message);
    }


    // previous method was causing errors, fixed the form and types
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error for session " + session.getId() + ": " + throwable.getMessage());
        throwable.printStackTrace();
    }

    /**
     * Called when a new notification is created
     */
    public static void sendNotification(@PathParam("userId") int userId, String message) {
        Session session = sessions.get(userId);
        if (session != null && session.getBasicRemote()!=null) {
            try {
                session.getBasicRemote().sendText(message);
                System.out.println("Send notification to user " + userId + ": " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No active websocket session for user " + userId);
        }
    }
}