package User_Info.websocket;

import User_Info.composite.Chat_MemberKey;
import User_Info.composite.Send_MessageKey;
import User_Info.model.*;
import User_Info.repository.Chat_MemberRepository;
import User_Info.repository.Chat_MessageRepository;
import User_Info.repository.Send_MessageRepository;
import User_Info.repository.User_InfoRepository;
import User_Info.service.Chat_RoomService;
import User_Info.service.Send_MessageService;
import User_Info.service.User_InfoService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.*;

@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/chat/{chatroomId}/{id}")  // this is Websocket url
public class Chat_RoomSocket {



//        private Integer chatRoom;

        private static User_InfoService uIS;

        //private static Chat_MessageRepository cmsgRepo;

        //private static User_InfoRepository cusrRepo;

        private static Chat_RoomService chtRS;

//        private static Chat_MemberRepository chtMRepo;

        private static Send_MessageService sndMsgS;


//        public void setChtRmID(@PathParam("chatroomId") Integer chatroomId){
//            this.chatRoom = chatroomId;
//        }

//        @Autowired
//        public void setMessageRepository(Chat_MessageRepository repo) {
//            cmsgRepo = repo;  // we are setting the static variable
//        }

//        @Autowired
//        public void setUser_InfoRepository(User_InfoRepository repo) {
//            cusrRepo = repo;
//        }

//        @Autowired
//        public void setChat_MemberRepository(Chat_MemberRepository chtMR){
//            chtMRepo = chtMR;
//        }

        @Autowired
        public void setUser_InfoService(User_InfoService usrInfoService){
            uIS = usrInfoService;
        }

        @Autowired
        public void setChat_MemberService(Chat_RoomService crService){
            chtRS = crService;
        }

        @Autowired
        public void setSend_MessageService(Send_MessageService smService){
            sndMsgS = smService;
        }

// Store all socket session and their corresponding username.
    public static Map<Session, Chat_MemberKey> sessionChatMemberMap = new Hashtable<>();
    public static Map<Chat_MemberKey, Session> ChatMemberSessionMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(Chat_RoomSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("chatroomId") Integer chatroomId, @PathParam("id") Integer id) throws IOException {

        logger.info("Entered into Open");

//        Chat_MemberKey cmk = new Chat_MemberKey(id, chatroomId);

//        try {
            // throws exception
            //User_Info cu = uIS.userLoginById(id, password);
            Optional<User_Info> cuo = uIS.serviceFindById(id);
            if (cuo.isEmpty()) {
                System.out.println("That id is not associated with a user. Remember to use the INTEGER id of the User_Info, not the email");
                return;
            }
            User_Info cu = cuo.get();
            Chat_MemberKey cmk = new Chat_MemberKey(id, chatroomId);

            if (ChatMemberSessionMap.containsKey(cmk)){
                session.getBasicRemote().sendText("Error: You are already connected to this session. Please try again later.");
                session.close();
                return;
            }

            if (chtRS.serviceFindById(cmk).isPresent()){
            //if (chtMRepo.findById(cmk).isPresent()){
                sessionChatMemberMap.put(session, cmk);
                ChatMemberSessionMap.put(cmk, session);
            } else {
                session.getBasicRemote().sendText("Error: You do not have permission to access this chat room!");
                session.close();
                return;
            }

                //Send chat history to the newly connected user
                sendMessageToParticularCMK(cmk);

                //String message = "New User:" + cu.getProfile().getProfileName() + " has Joined the Chat";
                String message = "New User: " + cu.getEmailId() + " has Joined the Chat";
                Send_Message sm = sndMsgS.createMsgToSend(cmk, message);
                broadcastToOneRm(sm);

//        } catch (RuntimeException | IOException e) {
//            session.getBasicRemote().sendText("Incorrect login info");
//            session.close();
            //throw new RuntimeException(e);
//        }

    }



    @OnMessage
    public void onMessage(Session session, String message) throws RuntimeException {


        // Handle new messages
        logger.info("Entered into Message: Got Message:" + message);
        Chat_MemberKey cmk = sessionChatMemberMap.get(session);
        Optional<User_Info> oUser = uIS.serviceFindById(cmk.getUserId());
//        Optional<User_Info> oUser = cusrRepo.findById(cmk.getUserId());

        if (oUser.isEmpty()) {
            throw new RuntimeException("This user should no longer be mapped in the session...");
        } else {
            User_Info user = oUser.get();
            String username = user.getUserName();
            String sendMessage = username + ": " + message;


            Send_Message sM = sndMsgS.createMsgToSend(cmk, sendMessage);
            broadcastToOneRm(sM);


            //testing purposes only
//            ArrayList<Integer> arrList = new ArrayList<Integer>();
//            arrList.add(1);
//            arrList.add(3);
//            sndMsgS.broadcastToMultRmS(arrList, user, sendMessage, ChatMemberSessionMap, logger);

        }
    }



    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");

        // remove the user connection information
        Chat_MemberKey cmk = sessionChatMemberMap.get(session);
        if (cmk != null) {
            Optional<User_Info> oUser = uIS.serviceFindById(cmk.getUserId());
//            Optional<User_Info> oUser = cusrRepo.findById(cmk.getUserId());
            if (oUser.isPresent()) {
                sessionChatMemberMap.remove(session);
                ChatMemberSessionMap.remove(cmk);
            } else {
                throw new RuntimeException("This user should no longer be mapped in the session...");
//            sessionChatUserMap.remove(session);
//            ChatUserSessionMap.remove(userKey);
            }
        }

        // broadcast that the user disconnected
        //String message = oUser.get().getUserName() + " disconnected";
        //broadcast(message);
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }


    private void sendMessageToParticularCMK(Chat_MemberKey cmk) {
        sndMsgS.sendMessageToParticularCMKS(cmk, ChatMemberSessionMap, logger);
    }


    private void broadcastToOneRm(Send_Message sm) {
        sndMsgS.broadcastToOneRmS(sm, ChatMemberSessionMap, logger);
    }


    // Gets the Chat history from the repository
    private String getChatHistory(int crId) {
        return sndMsgS.getChatHistoryS(crId);
    }

} // end of Class

