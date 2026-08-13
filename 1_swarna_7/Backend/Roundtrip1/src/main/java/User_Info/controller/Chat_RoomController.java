package User_Info.controller;

import User_Info.model.Chat_Member;
import User_Info.model.Event_Chat_Room;
import User_Info.model.Events;
import User_Info.model.Priv_Chat_Room;
import User_Info.repository.Chat_RoomRepository;
import User_Info.repository.EventsRepository;
import User_Info.repository.User_InfoRepository;
import User_Info.service.Chat_RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class Chat_RoomController {

    @Autowired
    User_InfoRepository User_InfoRepository;

    @Autowired
    Chat_RoomService Chat_RoomService;

//    @Autowired
//    Event_Chat_RoomRepository Event_Chat_RoomRepository;

//    @Autowired
//    Friend_Chat_RoomRepository Friend_Chat_RoomRepository;

//    @Autowired
//    Priv_Chat_RoomRepository Priv_Chat_RoomRepository;

    @Autowired
    EventsRepository EventsRepository;

    @Autowired
    Chat_RoomRepository Chat_RoomRepository;

//    @Autowired
//    User_InfoService User_InfoService;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    // Operations
//    @Operation(summary = "Create private chatroom (id is generated)")
//    @PostMapping("api/create/privchat")
//    Priv_Chat_Room createPrivChatRoom() {
//        return Chat_RoomService.createPrivRoom();
//    }

    @Operation(summary = "Create event chatroom associated with eventId")
    @PostMapping("api/create/eventchat/{eventId}")
    public Event_Chat_Room createEventChatRoom(@PathVariable int eventId) {
        Optional<Events> oe = EventsRepository.findById(eventId);
        if (oe.isEmpty()){
            throw new RuntimeException("Events id does not exist");
        }
        Events e = oe.get();
        return Chat_RoomService.createEventRoom(e);
    }

    @Operation(summary = "Add user by userId to chatroom by chatroomId")
    @PutMapping("api/addmember/private/chat/{crId}/{userId}")
    public Optional<Chat_Member> addChatMember(@PathVariable int crId, @PathVariable int userId) {
        if (User_InfoRepository.findById(userId).isPresent() && Chat_RoomRepository.findById(crId).isPresent()) {
            return Chat_RoomService.addToChatRoom(User_InfoRepository.findById(userId).get(), Chat_RoomRepository.findById(crId).get());
        } else {
            return Optional.empty();
        }
    }

    //@GetMapping()
    @Operation(summary = "Delete chatroom by chatroomId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted chat room",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Chat room not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("api/delete/chat/{id}")
    public ResponseEntity<Object> deleteChatRoom(@PathVariable int id) {

        //if (cr.isPresent()) {
        if (Chat_RoomRepository.findById(id).isPresent()) {
            Chat_RoomRepository.delete(Chat_RoomRepository.findById(id).get());
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("Chat_Room_Id: ", String.valueOf(id));
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Chat_Room_Id '" + id + "' not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }


    }

    @GetMapping("api/chat/friendChats/{userId}")
    public ResponseEntity<?> getUsersPrivateChatroomsId(@PathVariable int userId){
        Chat_RoomService.getAllAssociatePrivateChats(userId);
        try {
            return ResponseEntity.ok(Chat_RoomService.getAllAssociatePrivateChats(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
