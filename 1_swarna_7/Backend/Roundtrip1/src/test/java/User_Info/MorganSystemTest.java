package User_Info;

import User_Info.controller.Chat_RoomController;
import User_Info.controller.FriendshipController;
import User_Info.model.*;
import User_Info.repository.*;
import User_Info.service.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {FriendshipController.class,  Chat_RoomController.class})
@AutoConfigureMockMvc(addFilters = false)
@Import({Map_NodeService.class, AdminService.class})
public class MorganSystemTest {

    private static final String FRIEND_URL = "/api/friends";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Map_NodeService mapNodeService;

    @Autowired
    private AdminService adminService;

    @MockitoBean
    private FriendshipService friendshipService;

    @MockitoBean
    private EventsRepository eventsRepository;

    @MockitoBean
    private User_InfoService user_InfoService;

    @MockitoBean
    private Map_NodeRepository mapNodeRepository;

    @MockitoBean
    private User_InfoRepository user_InfoRepository;

    @MockitoBean
    private AdminRepository adminRepository;

    @MockitoBean
    private Admin_IssueRepository admin_IssueRepository;

    @MockitoBean
    private Admin_Issue_EventRepository admin_Issue_EventRepository;

    @MockitoBean
    private Admin_Issue_UserRepository admin_Issue_UserRepository;

    @Autowired
    private Chat_RoomController chatRoomController;

    @MockitoBean
    private Chat_RoomService Chat_RoomService;

    @MockitoBean
    private Chat_RoomRepository Chat_RoomRepository;

    // System Cases: FriendshipController (Morgan's methods)
    @Test
    public void testGetFriendsInterestedEventsReturnsList() throws Exception {
        int userId = 1;
        FriendshipService.FriendInterestedEventsDto dto1 = mock(FriendshipService.FriendInterestedEventsDto.class);
        FriendshipService.FriendInterestedEventsDto dto2 = mock(FriendshipService.FriendInterestedEventsDto.class);
        List<FriendshipService.FriendInterestedEventsDto> expected = Arrays.asList(dto1, dto2);
        when(friendshipService.getFriendsInterestedEvents(userId)).thenReturn(expected);
        mockMvc.perform(get(FRIEND_URL + "/" + userId + "/friendsInterestedEvents")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
        verify(friendshipService).getFriendsInterestedEvents(userId);
        verifyNoMoreInteractions(friendshipService);
    }

    @Test
    public void testGetFriendsInterestedEventsReturnsEmptyList() throws Exception {
        int userId = 2;
        when(friendshipService.getFriendsInterestedEvents(userId)).thenReturn(Collections.emptyList());
        mockMvc.perform(get(FRIEND_URL + "/" + userId + "/friendsInterestedEvents")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        verify(friendshipService).getFriendsInterestedEvents(userId);
        verifyNoMoreInteractions(friendshipService);
    }

    @Test
    public void getFriendsAttendingEventNotFound() throws Exception {
        int userId = 1;
        int eventId = 999;
        when(eventsRepository.findById(eventId)).thenReturn(Optional.empty());
        mockMvc.perform(get(FRIEND_URL + "/" + userId + "/friendsAttending/" + eventId)).andExpect(status().isNotFound()).andExpect(content().string("Event not found associated with that id"));
        verify(eventsRepository).findById(eventId);
        verify(friendshipService, never()).getFriendsAttendingEvent(anyInt(), any(Events.class));
    }

    @Test
    public void testGetFriendsAttendingEventFoundReturnsList() throws Exception {
        int userId = 1;
        int eventId = 42;

        Events event = new Events();
        when(eventsRepository.findById(eventId)).thenReturn(Optional.of(event));
        FriendshipService.FriendAttendingEventDto dto1 = mock(FriendshipService.FriendAttendingEventDto.class);
        FriendshipService.FriendAttendingEventDto dto2 = mock(FriendshipService.FriendAttendingEventDto.class);
        List<FriendshipService.FriendAttendingEventDto> expected = Arrays.asList(dto1, dto2);
        when(friendshipService.getFriendsAttendingEvent(userId, event)).thenReturn(expected);
        mockMvc.perform(get(FRIEND_URL + "/" + userId + "/friendsAttending/" + eventId)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));

        verify(eventsRepository, times(2)).findById(eventId);
        verify(friendshipService).getFriendsAttendingEvent(userId, event);
        verifyNoMoreInteractions(friendshipService);
    }

    @Test
    public void testGetFriendsAttendingEventFoundReturnsEmptyList() throws Exception {
        int userId = 3;
        int eventId = 77;

        Events event = new Events();
        when(eventsRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(friendshipService.getFriendsAttendingEvent(userId, event)).thenReturn(Collections.emptyList());
        mockMvc.perform(get(FRIEND_URL + "/" + userId + "/friendsAttending/" + eventId)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        verify(eventsRepository, times(2)).findById(eventId);
        verify(friendshipService).getFriendsAttendingEvent(userId, event);
        verifyNoMoreInteractions(friendshipService);
    }

    @Test
    public void testAcceptFriendRequestCreatesChatroom() throws Exception {
        int userId = 1;
        int friendId = 2;
        when(friendshipService.acceptRequest(userId, friendId)).thenReturn("Friend request accepted.");
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/friends/accept/" + userId + "/" + friendId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.friendId").value(friendId));

        verify(friendshipService).acceptRequest(userId, friendId);
    }


    // System Cases: Chat_RoomController
    @Test
    public void testCreateEventChatRoomSuccess() {
        int eventId = 10;
        Events e = new Events();

        Event_Chat_Room mockRoom = mock(Event_Chat_Room.class);
        when(eventsRepository.findById(eventId)).thenReturn(Optional.of(e));
        when(Chat_RoomService.createEventRoom(e)).thenReturn(mockRoom);
        Event_Chat_Room result = chatRoomController.createEventChatRoom(eventId);
        assertSame(mockRoom, result);
        verify(eventsRepository).findById(eventId);
        verify(Chat_RoomService).createEventRoom(e);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateEventChatRoomMissingEventError() {
        int eventId = 999;
        when(eventsRepository.findById(eventId)).thenReturn(Optional.empty());
        chatRoomController.createEventChatRoom(eventId);
    }

    @Test
    public void testAddChatMemberSuccess() {
        int crId = 5;
        int userId = 7;

        User_Info u = new User_Info();
        Chat_Room c = mock(Chat_Room.class);
        Chat_Member cm = mock(Chat_Member.class);

        when(user_InfoRepository.findById(userId)).thenReturn(Optional.of(u));
        when(Chat_RoomRepository.findById(crId)).thenReturn(Optional.of(c));
        when(Chat_RoomService.addToChatRoom(u, c)).thenReturn(Optional.of(cm));

        Optional<Chat_Member> result = chatRoomController.addChatMember(crId, userId);

        assertTrue(result.isPresent());
        assertSame(cm, result.get());

        verify(Chat_RoomService).addToChatRoom(u, c);
    }

    @Test
    public void testAddChatMemberMissingUserOrMissingChatReturnsEmpty() {
        int crId = 5;
        int userId = 7;

        when(user_InfoRepository.findById(userId)).thenReturn(Optional.empty());
        when(Chat_RoomRepository.findById(crId)).thenReturn(Optional.of(mock(Chat_Room.class)));

        Optional<Chat_Member> result = chatRoomController.addChatMember(crId, userId);

        assertTrue(result.isEmpty());
        verify(Chat_RoomService, never()).addToChatRoom(any(), any());
    }

    @Test
    public void testDeleteChatRoomSuccess() {
        int crId = 12;
        Chat_Room room = mock(Chat_Room.class);
        when(Chat_RoomRepository.findById(crId)).thenReturn(Optional.of(room));
        ResponseEntity<Object> res = chatRoomController.deleteChatRoom(crId);
        assertEquals(HttpStatus.OK, res.getStatusCode());;
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) res.getBody();
        assertEquals("success", body.get("status"));
        verify(Chat_RoomRepository).delete(room);
    }

    @Test
    public void testDeleteChatRoomMissingError() {
        int crId = 888;
        when(Chat_RoomRepository.findById(crId)).thenReturn(Optional.empty());

        ResponseEntity<Object> res = chatRoomController.deleteChatRoom(crId);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) res.getBody();
        assertEquals("error", body.get("status"));
    }

    @Test
    public void testGetUsersPrivateChatroomsIdReturnsList() {
        int userId = 3;

        List<Chat_RoomService.friendChat> expected = new ArrayList<>();
        when(Chat_RoomService.getAllAssociatePrivateChats(userId)).thenReturn(expected);
        ResponseEntity<?> res = chatRoomController.getUsersPrivateChatroomsId(userId);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(expected, res.getBody());

        // Note: controller currently calls this twice
        verify(Chat_RoomService, times(2)).getAllAssociatePrivateChats(userId);
    }


    // System Cases: Map_NodeService
    // prefers an event-associated neighbor even if a non-associated node
    // is closer, as long as both are within mergeScrutiny.
    @Test
    public void testCheckMergePrioEventInsideScrutiny() {
        Events e = new Events();
        Map_Node closerNonAssociated = mock(Map_Node.class, RETURNS_DEEP_STUBS);
        when(closerNonAssociated.getLatitude()).thenReturn(0.1);
        when(closerNonAssociated.getLongitude()).thenReturn(0.1);
        when(closerNonAssociated.getMergeScrutiny()).thenReturn(10.0);
        when(closerNonAssociated.getAssociatedEvents().contains(any(Events.class))).thenReturn(false);

        Map_Node fartherAssociated = mock(Map_Node.class, RETURNS_DEEP_STUBS);
        when(fartherAssociated.getLatitude()).thenReturn(3.0);
        when(fartherAssociated.getLongitude()).thenReturn(3.0);
        when(fartherAssociated.getMergeScrutiny()).thenReturn(10.0);
        when(fartherAssociated.getAssociatedEvents().contains(e)).thenReturn(true);
        when(mapNodeRepository.findAll()).thenReturn(Arrays.asList(closerNonAssociated, fartherAssociated));
        Optional<Map_Node> result = mapNodeService.checkMergePrioEvent(e, 0.0, 0.0);

        assertTrue("Expected a neighbor to be found", result.isPresent());
        assertSame("Service should prioritize event-associated node over closer non-associated node", fartherAssociated, result.get());
    }

    // returns empty when no nodes fall within their mergeScrutiny radius.
    @Test
    public void testCheckMergePrioEventOutsideScrutiny() {
        Map_Node farNode = mock(Map_Node.class, RETURNS_DEEP_STUBS);
        when(farNode.getLatitude()).thenReturn(100.0);
        when(farNode.getLongitude()).thenReturn(100.0);
        when(farNode.getMergeScrutiny()).thenReturn(1.0);
        when(farNode.getAssociatedEvents().contains(any(Events.class))).thenReturn(false);

        when(mapNodeRepository.findAll()).thenReturn(Collections.singletonList(farNode));

        Optional<Map_Node> result = mapNodeService.checkMergePrioEvent(new Events(), 0.0, 0.0);

        assertFalse("Expected no neighbor when everything is outside mergeScrutiny", result.isPresent());
    }

    // calls deleteEventAssociatedMapNode for each associated node.
//    @Test
//    public void testHandleRemoveEventFromAssocNodes() {
//        Events e = new Events();
//        Map_Node node1 = mock(Map_Node.class);
//        Map_Node node2 = mock(Map_Node.class);
//
//        when(mapNodeRepository.findAllByAssociatedEventsContaining(e)).thenReturn(Arrays.asList(node1, node2));
//        Map_NodeService spyService = spy(mapNodeService);
//        doNothing().when(spyService).deleteEventAssociatedMapNode(any(Map_Node.class), eq(e));
//        spyService.handleRemoveEventFromAssocNodes(e);
//        verify(spyService).deleteEventAssociatedMapNode(node1, e);
//        verify(spyService).deleteEventAssociatedMapNode(node2, e);
//    }

    // AdminService Tests

}
