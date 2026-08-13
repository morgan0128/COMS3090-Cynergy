package User_Info;

import User_Info.composite.Chat_MemberKey;
import User_Info.composite.Send_MessageKey;
import User_Info.model.*;
import User_Info.repository.*;
import User_Info.service.Chat_RoomService;
import User_Info.service.Send_MessageService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class MorganChatRoomServiceCoverageTest {

    @Mock private User_InfoRepository userRepo;
    @Mock private Event_Chat_RoomRepository eventRepo;
    @Mock private Priv_Chat_RoomRepository privRepo;
    @Mock private Chat_MemberRepository memberRepo;
    @Mock private Chat_MessageRepository msgRepo;
    @Mock private Send_MessageRepository sendRepo;

    // Send_MessageService depends on Chat_RoomService:
    // Require Mock of Chat_RoomService
    @Mock private Chat_RoomService chatRoomServiceMock;

    @InjectMocks
    private Send_MessageService sendMessageService;

    @InjectMocks
    private Chat_RoomService service;


    @Test
    public void testGetAllPublicChatroomsReturnsEventRepoFindAll() {
        List<Event_Chat_Room> rooms = Arrays.asList(mock(Event_Chat_Room.class), mock(Event_Chat_Room.class));
        when(eventRepo.findAll()).thenReturn(rooms);
        List<? extends Chat_Room> result = service.getAllPublicChatrooms();
        assertSame(rooms, result);
        verify(eventRepo).findAll();
    }

    @Test
    public void testCreateEventRoomSavesRoomAddsAll() {
        Events e = new Events();
        User_Info u1 = new User_Info(); u1.setId(1);
        User_Info u2 = new User_Info(); u2.setId(2);

        when(userRepo.findAll()).thenReturn(Arrays.asList(u1, u2));
        Event_Chat_Room saved = mock(Event_Chat_Room.class);
        when(eventRepo.save(any(Event_Chat_Room.class))).thenReturn(saved);
        Chat_RoomService spy = spy(service);
        doReturn(Optional.empty()).when(spy).addToChatRoom(any(User_Info.class), any(Chat_Room.class));
        Event_Chat_Room result = spy.createEventRoom(e);
        assertSame(saved, result);
        ArgumentCaptor<User_Info> userCap = ArgumentCaptor.forClass(User_Info.class);
        ArgumentCaptor<Chat_Room> roomCap = ArgumentCaptor.forClass(Chat_Room.class);
        verify(spy, times(2)).addToChatRoom(userCap.capture(), roomCap.capture());
        List<User_Info> calledUsers = userCap.getAllValues();
        assertTrue(calledUsers.contains(u1));
        assertTrue(calledUsers.contains(u2));
        assertEquals(2, roomCap.getAllValues().size());
        assertSame(roomCap.getAllValues().get(0), roomCap.getAllValues().get(1));
    }

    @Test
    public void testCreatePrivRoom() {
        int f1 = 1, f2 = 2;
        User_Info u1 = new User_Info(); u1.setId(f1);
        User_Info u2 = new User_Info(); u2.setId(f2);

        when(userRepo.findById(f1)).thenReturn(Optional.of(u1));
        when(userRepo.findById(f2)).thenReturn(Optional.of(u2));
        Priv_Chat_Room savedRoom = mock(Priv_Chat_Room.class);
        when(savedRoom.getChatRoom_id()).thenReturn(100);
        when(privRepo.save(any(Priv_Chat_Room.class))).thenReturn(savedRoom);
        Chat_RoomService spy = spy(service);
        doReturn(false).when(spy).inChatRoom(any(Chat_MemberKey.class));
        when(memberRepo.save(any(Chat_Member.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepo.findById(any(Chat_MemberKey.class))).thenAnswer(inv -> Optional.of(new Chat_Member(inv.getArgument(0))));
        Priv_Chat_Room result = spy.createPrivRoom(f1, f2);
        assertSame(savedRoom, result);
        verify(spy, times(2)).addToChatRoom(any(User_Info.class), eq(savedRoom));
    }

    @Test(expected = RuntimeException.class)
    public void testCreatePrivRoomWithMissingUser() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());
        service.createPrivRoom(1, 2);
    }

    @Test
    public void testInChatRoom() {
        Chat_MemberKey key = new Chat_MemberKey(1, 10);
        when(memberRepo.findById(key)).thenReturn(Optional.of(new Chat_Member(key)));

        assertTrue(service.inChatRoom(key));
    }

    @Test
    public void testGetChatRoomMembersReturnsNull() {
        Chat_Room c = mock(Chat_Room.class);
        assertNull(service.getChatRoomMembers(c));
    }

    @Test
    public void testAddToChatRoomReturnsEmptyIfMember() {
        User_Info u = new User_Info(); u.setId(1);
        Chat_Room c = mock(Chat_Room.class);
        when(c.getChatRoom_id()).thenReturn(10);

        Chat_RoomService spy = spy(service);
        doReturn(true).when(spy).inChatRoom(any(Chat_MemberKey.class));
        Optional<Chat_Member> result = spy.addToChatRoom(u, c);

        assertTrue(result.isEmpty());
        verify(memberRepo, never()).save(any(Chat_Member.class));
    }

    @Test
    public void testAddToChatRoomSavesAndReturnsMemberNew() {
        User_Info u = new User_Info(); u.setId(1);
        Chat_Room c = mock(Chat_Room.class);
        when(c.getChatRoom_id()).thenReturn(10);

        Chat_RoomService spy = spy(service);
        doReturn(false).when(spy).inChatRoom(any(Chat_MemberKey.class));

        when(memberRepo.save(any(Chat_Member.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepo.findById(any(Chat_MemberKey.class))).thenAnswer(inv -> Optional.of(new Chat_Member(inv.getArgument(0))));

        Optional<Chat_Member> result = spy.addToChatRoom(u, c);

        assertTrue(result.isPresent());
        verify(memberRepo).save(any(Chat_Member.class));
    }

    @Test
    public void testAddToAllPublicChatRoomSavesOnlyForRoomsUserNotIn() {
        User_Info u = new User_Info(); u.setId(5);

        Chat_Room r1 = mock(Chat_Room.class);
        Chat_Room r2 = mock(Chat_Room.class);
        when(r1.getChatRoom_id()).thenReturn(11);
        when(r2.getChatRoom_id()).thenReturn(22);
        Chat_RoomService spy = spy(service);
        doReturn(Arrays.asList(r1, r2)).when(spy).getAllPublicChatrooms();
        doReturn(true, false).when(spy).inChatRoom(any(Chat_MemberKey.class));
        spy.addToAllPublicChatRoom(u);
        verify(memberRepo, times(1)).save(any(Chat_Member.class));
    }

    @Test
    public void testRemoveFromChatRoomNotMember() {
        User_Info u = new User_Info(); u.setId(1);
        Chat_Room c = mock(Chat_Room.class);
        when(c.getChatRoom_id()).thenReturn(10);

        Chat_RoomService spy = spy(service);
        doReturn(false).when(spy).inChatRoom(any(Chat_MemberKey.class));
        assertFalse(spy.removeFromChatRoom(u, c));
        verify(memberRepo, never()).delete(any(Chat_Member.class));
    }

    @Test
    public void testRemoveFromChatRoomDeletes() {
        User_Info u = new User_Info(); u.setId(1);
        Chat_Room c = mock(Chat_Room.class);
        when(c.getChatRoom_id()).thenReturn(10);

        Chat_RoomService spy = spy(service);
        doReturn(true).when(spy).inChatRoom(any(Chat_MemberKey.class));

        assertTrue(spy.removeFromChatRoom(u, c));
        verify(memberRepo).delete(any(Chat_Member.class));
    }

    @Test
    public void testGetChatMByKeyReturnsMember() {
        Chat_MemberKey key = new Chat_MemberKey(1, 10);
        Chat_Member cm = new Chat_Member(key);

        when(memberRepo.findById(key)).thenReturn(Optional.of(cm));

        assertSame(cm, service.getChatMByKey(key));
    }

    @Test
    public void testGetChatMByKeyMissingMember() {
        Chat_MemberKey key = new Chat_MemberKey(1, 10);
        when(memberRepo.findById(key)).thenReturn(Optional.empty());

        assertNull(service.getChatMByKey(key));
    }

    @Test
    public void testGetChatMRmByKey() {
        Chat_MemberKey key = new Chat_MemberKey(1, 10);
        Chat_Member cm = new Chat_Member(key);

        when(memberRepo.findById(key)).thenReturn(Optional.of(cm));

        Optional<Chat_Member> result = service.getChatMRmByKey(key);
        assertTrue(result.isPresent());
    }

    @Test
    public void testSaveToMessageRepo() {
        Chat_Message cm = mock(Chat_Message.class);
        service.saveToRepo(cm);
        verify(msgRepo).save(cm);
    }

    @Test
    public void testFindAllMsgsInOneRoom() {
        int crId = 55;

        Send_Message sm1 = mock(Send_Message.class);
        Send_Message sm2 = mock(Send_Message.class);
        when(sm1.getMessageId()).thenReturn(1L);
        when(sm2.getMessageId()).thenReturn(2L);
        when(sendRepo.findAllBySmkIdChatRoomId(crId)).thenReturn(Arrays.asList(sm1, sm2));
        Chat_Message msg1 = mock(Chat_Message.class);
        when(msgRepo.findById(1L)).thenReturn(Optional.of(msg1));
        when(msgRepo.findById(2L)).thenReturn(Optional.empty());
        List<Chat_Message> result = service.findAllMsgsInOneR(crId);
        assertEquals(1, result.size());
        assertSame(msg1, result.get(0));
    }

    @Test
    public void testServiceFindById() {
        Chat_MemberKey key = new Chat_MemberKey(1, 10);
        when(memberRepo.findById(key)).thenReturn(Optional.empty());
        assertTrue(service.serviceFindById(key).isEmpty());
        verify(memberRepo).findById(key);
    }

    @Test
    public void testGetAllEventChatReturnsEventRepoFindAll() {
        List<Event_Chat_Room> rooms = Collections.singletonList(mock(Event_Chat_Room.class));
        when(eventRepo.findAll()).thenReturn(rooms);
        List<Event_Chat_Room> result = service.getAllEventChat();
        assertSame(rooms, result);
        verify(eventRepo).findAll();
    }

    @Test
    public void testGetAllAssociatePrivateChats() {
        int userId = 1;

        User_Info main = new User_Info();
        main.setId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(main));
        Priv_Chat_Room pAsF1 = mock(Priv_Chat_Room.class);
        when(pAsF1.getChatRoom_id()).thenReturn(100);
        when(pAsF1.getFriend2Id()).thenReturn(2);
        Priv_Chat_Room pAsF2 = mock(Priv_Chat_Room.class);
        when(pAsF2.getChatRoom_id()).thenReturn(200);
        when(pAsF2.getFriend1Id()).thenReturn(3);
        when(privRepo.findDistinctByFriendOneIdEquals(userId)).thenReturn(Collections.singletonList(pAsF1));
        when(privRepo.findDistinctByFriendTwoIdEquals(userId)).thenReturn(Collections.singletonList(pAsF2));
        User_Info friend2 = new User_Info(); friend2.setId(2); friend2.setUserName("two");
        User_Info friend3 = new User_Info(); friend3.setId(3); friend3.setUserName("three");
        when(userRepo.findById(2)).thenReturn(Optional.of(friend2));
        when(userRepo.findById(3)).thenReturn(Optional.of(friend3));
        List<Chat_RoomService.friendChat> result = service.getAllAssociatePrivateChats(userId);
        assertEquals(2, result.size());

        Chat_RoomService.friendChat fc0 = result.get(0);
        fc0.getChatId();
        fc0.getFriendUserId();
        fc0.getFriendUserName();
    }

    @Test(expected = RuntimeException.class)
    public void testGetAllAssociatePrivateChatsMissingUser() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());
        service.getAllAssociatePrivateChats(1);
    }

    @Test
    public void testSendMessageModelClassGetters() {
        Send_MessageKey smk = new Send_MessageKey(99L, 123);
        Send_Message sm = new Send_Message(smk);

        assertEquals(Integer.valueOf(123), sm.getChatRoomId());
        assertEquals(Long.valueOf(99L), sm.getMessageId());
        assertSame(smk, sm.getSMK());
    }

    @Test
    public void testSendMessageServiceReturnsMessageString() {
        Send_MessageKey smk = new Send_MessageKey(5L, 10);
        Send_Message sm = new Send_Message(smk);

        Chat_Message cm = mock(Chat_Message.class);
        when(cm.getContent()).thenReturn("sup guys");
        when(msgRepo.findById(5L)).thenReturn(Optional.of(cm));
        String result = sendMessageService.getMsgString(sm);
        assertEquals("sup guys", result);
        verify(msgRepo).findById(5L);
    }

    @Test
    public void testSendMessageServiceGetMsgStringMessageMissingReturnsEmptyString() {
        Send_MessageKey smk = new Send_MessageKey(6L, 10);
        Send_Message sm = new Send_Message(smk);
        when(msgRepo.findById(6L)).thenReturn(Optional.empty());
        String result = sendMessageService.getMsgString(sm);

        assertEquals("", result);
        verify(msgRepo).findById(6L);
    }

    @Test
    public void testSendMessageServiceGetChatHistoryS() {
        int roomId = 77;

        Chat_Message m1 = mock(Chat_Message.class);
        when(m1.getUserName()).thenReturn("alice");
        when(m1.getContent()).thenReturn("hi");

        Chat_Message m2 = mock(Chat_Message.class);
        when(m2.getUserName()).thenReturn("bob");
        when(m2.getContent()).thenReturn("yo");

        when(chatRoomServiceMock.findAllMsgsInOneR(roomId)).thenReturn(Arrays.asList(m1, m2));

        String history = sendMessageService.getChatHistoryS(roomId);
        assertTrue(history.contains("alice: hi"));
        assertTrue(history.contains("bob: yo"));
        assertTrue(history.endsWith("\n"));
        verify(chatRoomServiceMock).findAllMsgsInOneR(roomId);
    }

    @Test
    public void testCreateMsgToSend() {
        int userId = 8;
        int roomId = 33;
        Chat_MemberKey cmk = new Chat_MemberKey(userId, roomId);
        Chat_Member member = mock(Chat_Member.class);
        when(member.getUserId()).thenReturn(userId);
        when(chatRoomServiceMock.getChatMByKey(cmk)).thenReturn(member);

        // User exists
        User_Info user = new User_Info();
        user.setId(userId);
        user.setUserName("tester");
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // Saving Chat_Message returns a message with an ID
        Chat_Message savedMsg = mock(Chat_Message.class);
        when(savedMsg.getId()).thenReturn(500L);
        when(msgRepo.save(any(Chat_Message.class))).thenReturn(savedMsg);
        when(sendRepo.save(any(Send_Message.class))).thenAnswer(inv -> inv.getArgument(0));

        Send_Message sm = sendMessageService.createMsgToSend(cmk, "hello!");
        assertNotNull(sm);
        assertEquals(Integer.valueOf(roomId), sm.getChatRoomId());
        assertEquals(Long.valueOf(500L), sm.getMessageId());

        verify(chatRoomServiceMock).getChatMByKey(cmk);
        verify(userRepo).findById(userId);
        verify(msgRepo).save(any(Chat_Message.class));
        verify(sendRepo).save(any(Send_Message.class));
    }

    @Test
    public void testCreateMsgToSendUserMissingReturnsNull() {
        int userId = 9;
        int roomId = 44;
        Chat_MemberKey cmk = new Chat_MemberKey(userId, roomId);

        Chat_Member member = mock(Chat_Member.class);
        when(member.getUserId()).thenReturn(userId);
        when(chatRoomServiceMock.getChatMByKey(cmk)).thenReturn(member);
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        Send_Message sm = sendMessageService.createMsgToSend(cmk, "no user");
        assertNull(sm);
        verify(chatRoomServiceMock).getChatMByKey(cmk);
        verify(userRepo).findById(userId);
        verify(msgRepo, never()).save(any(Chat_Message.class));
        verify(sendRepo, never()).save(any(Send_Message.class));
    }
}
