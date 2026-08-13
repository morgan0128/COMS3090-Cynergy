package User_Info.service;

import User_Info.composite.Chat_MemberKey;
import User_Info.model.*;
import User_Info.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class Chat_RoomService {

    @Autowired
    User_InfoRepository User_InfoRepository;

    @Autowired
    Event_Chat_RoomRepository Event_Chat_RoomRepository;

    @Autowired
    Priv_Chat_RoomRepository Priv_Chat_RoomRepository;

    @Autowired
    Chat_MemberRepository Chat_MemberRepository;

    @Autowired
    Chat_MessageRepository Chat_MessageRepository;

    @Autowired
    Send_MessageRepository Send_MessageRepository;


    public List<? extends Chat_Room> getAllPublicChatrooms(){
        return Event_Chat_RoomRepository.findAll();
    }

    public Event_Chat_Room createEventRoom(Events e) {
        Event_Chat_Room eventChat = new Event_Chat_Room(e);
        Event_Chat_Room eventCR = Event_Chat_RoomRepository.save(eventChat);
        List<User_Info> usrList = User_InfoRepository.findAll();
        for (User_Info user : usrList) {
            addToChatRoom(user, eventChat);
        }
        return eventCR;
    }

    public Priv_Chat_Room createPrivRoom(int friend1Id, int friend2Id) {
        Priv_Chat_Room privChatRoom;
        if (User_InfoRepository.findById(friend1Id).isPresent() && User_InfoRepository.findById(friend2Id).isPresent()) {
            User_Info friend1 = User_InfoRepository.findById(friend1Id).get();
            User_Info friend2 = User_InfoRepository.findById(friend2Id).get();
            Priv_Chat_Room priv = new Priv_Chat_Room(friend1Id, friend2Id);
            privChatRoom = Priv_Chat_RoomRepository.save(priv);
            addToChatRoom(friend1, privChatRoom);
            addToChatRoom(friend2, privChatRoom);
        } else {
            throw new RuntimeException();
        }
        return privChatRoom;
    }

    public boolean inChatRoom(Chat_MemberKey cmk){
        return Chat_MemberRepository.findById(cmk).isPresent();
    }

    public Set<User_Info> getChatRoomMembers(Chat_Room c){
        //  return Chat_MemberRepository.getChat_MembersByChatRoom(c);
        return null;
    }

    public Optional<Chat_Member> addToChatRoom(User_Info u, Chat_Room c) {
        Chat_MemberKey cmk = new Chat_MemberKey(u.getId(), c.getChatRoom_id());
        if (inChatRoom(cmk)) {
            return Optional.empty();
        } else {
            Chat_Member cm = new Chat_Member(cmk);
            Chat_MemberRepository.save(cm);
            return Chat_MemberRepository.findById(cm.getCMK());
        }
    }

    public void addToAllPublicChatRoom(User_Info u) {
        List<? extends Chat_Room> chatRoomList = getAllPublicChatrooms();
        for (Chat_Room chatRoom : chatRoomList) {
            Chat_MemberKey cmk = new Chat_MemberKey(u.getId(), chatRoom.getChatRoom_id());
            if (!inChatRoom(cmk)) {
                Chat_Member cm = new Chat_Member(cmk);
                Chat_MemberRepository.save(cm);
            }
        }
    }

    public boolean removeFromChatRoom(User_Info u, Chat_Room c) {
        Chat_MemberKey cmk = new Chat_MemberKey(u.getId(), c.getChatRoom_id());
        if (!inChatRoom(cmk)) {
            return false;
        } else {
            Chat_Member cm = new Chat_Member(cmk);
            Chat_MemberRepository.delete(cm);
            return true;
        }
    }

    public Chat_Member getChatMByKey(Chat_MemberKey cmk){
        if (Chat_MemberRepository.findById(cmk).isPresent()) {
            return Chat_MemberRepository.findById(cmk).get();
        } else {
            return null;
        }
    }

    public Optional<Chat_Member> getChatMRmByKey(Chat_MemberKey cmk) {
        if (Chat_MemberRepository.findById(cmk).isPresent()) {
            return Chat_MemberRepository.findById(cmk);
        } else {
            return Optional.empty();
        }
    }


    public Integer getChatMUsrIdByKey(Chat_MemberKey cmk){
        if (Chat_MemberRepository.findById(cmk).isPresent()) {
            return  Chat_MemberRepository.findById(cmk).get().getUserId();
        } else {
            return null;
        }
    }

    public void saveToRepo(Chat_Message cm){
        Chat_MessageRepository.save(cm);
    }


    public List<Chat_Message> findAllMsgsInOneR(int crId) {
        List<Send_Message> lSM = Send_MessageRepository.findAllBySmkIdChatRoomId(crId);
        List<Chat_Message> lCM = new ArrayList<Chat_Message>();
        for (Send_Message sm : lSM) {
            Optional<Chat_Message> cm = Chat_MessageRepository.findById(sm.getMessageId());
            cm.ifPresent(lCM::add);
        }
        return lCM;
    }

    public Optional<Chat_Member> serviceFindById(Chat_MemberKey cmk) {
        return Chat_MemberRepository.findById(cmk);
    }

//    public void addToAllEventChat(User_Info user){
//        List<Event_Chat_Room> chatRoomList = getAllEventChat();
//        for (Event_Chat_Room chatRoom : chatRoomList) {
//            addToChatRoom(user, chatRoom);
//        }
//    }

    public List<Event_Chat_Room> getAllEventChat(){
        return Event_Chat_RoomRepository.findAll();
    }

    public class friendChat {
        int chatId;
        int friendUserId;
        String friendUserName;
        friendChat(){

        }
        friendChat(int cId, int fuId, String uName){
            this.chatId = cId;
            this.friendUserId = fuId;
            this.friendUserName = uName;
        }

        public int getChatId() {
            return chatId;
        }
        public int getFriendUserId(){
            return friendUserId;
        }

        public String getFriendUserName(){
            return friendUserName;
        }
    }

    public List<friendChat> getAllAssociatePrivateChats(int userId){
        List<friendChat> associatedRooms = new ArrayList<friendChat>();
        if (User_InfoRepository.findById(userId).isPresent()){
            List<Priv_Chat_Room> f2 = new ArrayList<Priv_Chat_Room>(Priv_Chat_RoomRepository.findDistinctByFriendOneIdEquals(userId));
            for (Priv_Chat_Room p : f2) {
                associatedRooms.add(new friendChat(p.getChatRoom_id(), p.getFriend2Id(), User_InfoRepository.findById(p.getFriend2Id()).orElseThrow().getUserName()));
            }
            List<Priv_Chat_Room> f1 = new ArrayList<Priv_Chat_Room>(Priv_Chat_RoomRepository.findDistinctByFriendTwoIdEquals(userId));
            for (Priv_Chat_Room p : f1) {
                associatedRooms.add(new friendChat(p.getChatRoom_id(), p.getFriend1Id(), User_InfoRepository.findById(p.getFriend1Id()).orElseThrow().getUserName()));
            }

        } else {
            throw new RuntimeException();
        }
        return associatedRooms;
    }

//    public Priv_Chat_Room createPrivRoom(int f1, int f2) {
//        Priv_Chat_Room priv = new Priv_Chat_Room();
//        priv.setFriendOne(f1);
//        priv.setFriendTwo(f2);
//
//        return Priv_Chat_RoomRepository.save(priv);
//    }

}
