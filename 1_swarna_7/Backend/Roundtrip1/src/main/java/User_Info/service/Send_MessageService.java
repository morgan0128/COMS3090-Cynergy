package User_Info.service;

import User_Info.composite.Chat_MemberKey;
import User_Info.composite.Send_MessageKey;
import User_Info.model.*;
import User_Info.repository.Chat_MemberRepository;
import User_Info.repository.Chat_MessageRepository;
import User_Info.repository.Send_MessageRepository;
import User_Info.repository.User_InfoRepository;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class Send_MessageService {

//    @Autowired
//    Priv_Chat_RoomRepository Priv_Chat_RoomRepository;

    @Autowired
    Chat_RoomService Chat_RoomService;


    @Autowired
    Chat_MessageRepository Chat_MessageRepository;

    @Autowired
    Send_MessageRepository Send_MessageRepository;

    @Autowired
    Chat_MemberRepository Chat_MemberRepository;

//    @Autowired
//    User_InfoService User_InfoService;

    @Autowired
    NotificationsService NotificationsService;

    @Autowired
    User_InfoRepository User_InfoRepository;

    public void broadcastToOneRmS(Send_Message sm, Map<Chat_MemberKey, Session> ChatMemberSessionMap, Logger logger) {
        distributeSMtoRoom(ChatMemberSessionMap, logger, sm);
    }

    public void broadcastToMultRmS(ArrayList<Integer> rooms, User_Info user, String message, Map<Chat_MemberKey, Session> ChatMemberSessionMap, Logger logger) {
        Chat_Message cm = createChatMessageInRepo(user, message);
        int uId = user.getId();
        for (Integer room : rooms) {
            Chat_MemberKey cmk = new Chat_MemberKey(uId, room);
            // Ensure broadcaster is a member of all rooms to send to (Note: this is an administrator feature)
            if (!Chat_MemberRepository.existsById(cmk)){
                Chat_Member chatMember = new Chat_Member(cmk);
                Chat_MemberRepository.save(chatMember);
            }
            Send_Message sm = createSendMessageInRepo(cm, cmk);
            distributeSMtoRoom(ChatMemberSessionMap, logger, sm);
        }
    }

    private void distributeSMtoRoom(Map<Chat_MemberKey, Session> ChatMemberSessionMap, Logger logger, Send_Message sm) {
        String toSend = getMsgString(sm);
        Chat_Message msgNotify = Chat_MessageRepository.findById(sm.getMessageId()).orElseThrow();
        ChatMemberSessionMap.forEach((cmKey, session) -> {
            try {
                if (cmKey.getChatRoomId().equals(sm.getChatRoomId())) {
                    session.getBasicRemote().sendText(toSend);
                    User_Info u = User_InfoRepository.findById(cmKey.getUserId()).orElseThrow();
                    NotificationsService.notifyChat(u, msgNotify);
                }
            } catch (IOException e) {
                logger.info("Exception: " + e.getMessage().toString());
                e.printStackTrace();
            }

        });
    }

    // Gets the Chat history from the repository
    public String getChatHistoryS(int crId) {
        List<Chat_Message> chat_messages = Chat_RoomService.findAllMsgsInOneR(crId);
        //List<Chat_Message> chat_messages = cmsgRepo.findAll();

        // convert the list to a string
        StringBuilder sb = new StringBuilder();
        if(!chat_messages.isEmpty()) {
            for (Chat_Message chat_message : chat_messages) {
                sb.append(chat_message.getUserName() + ": " + chat_message.getContent() + "\n");
            }
        }
        return sb.toString();
    }

    public void sendMessageToParticularCMKS(Chat_MemberKey cmk, Map<Chat_MemberKey, Session> ChatMemberSessionMap, Logger logger) {
        try {
            String message = getChatHistoryS(cmk.getChatRoomId());
            ChatMemberSessionMap.get(cmk).getBasicRemote().sendText(message);
        }
        catch (IOException e) {
            logger.info("Exception: " + e.getMessage().toString());
            e.printStackTrace();
        }
    }

    public Send_Message createMsgToSend(Chat_MemberKey cmk, String message){
        Optional<User_Info> ocu = User_InfoRepository.findById(Chat_RoomService.getChatMByKey(cmk).getUserId());
        if (ocu.isPresent()) {
            User_Info cu = ocu.get();
            Chat_Message cm = createChatMessageInRepo(cu, message);
            return createSendMessageInRepo(cm, cmk);
            //return new Send_Message(smk);
        } else {
            return null;
        }

    }

    public String getMsgString(Send_Message sm){
        Optional<Chat_Message>  cm = Chat_MessageRepository.findById(sm.getSMK().getMessageId());
        if (cm.isPresent()){
            return cm.get().getContent();
        } else {
            return "";
        }
    }

    public Chat_Message createChatMessageInRepo(User_Info cu, String message) {
        Chat_Message cm = new Chat_Message(cu.getUserName(), message, cu);
        return Chat_MessageRepository.save(cm);
    }

    public Send_Message createSendMessageInRepo(Chat_Message cm, Chat_MemberKey cmk){
        Send_MessageKey smk = new Send_MessageKey(cm.getId(), cmk.getChatRoomId());
        Send_Message sm = new Send_Message(smk);
        return Send_MessageRepository.save(sm);
    }


}
