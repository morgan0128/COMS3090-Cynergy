package User_Info.controller;

import User_Info.enumerator.Admin_Tier;
import User_Info.enumerator.Issue_Status_Type;
import User_Info.enumerator.Issue_Type;
import User_Info.model.*;
import User_Info.repository.AdminRepository;
import User_Info.repository.Admin_IssueRepository;
import User_Info.repository.EventsRepository;
import User_Info.repository.User_InfoRepository;
import User_Info.service.AdminService;
import User_Info.service.Chat_RoomService;
import User_Info.service.EventsService;
import User_Info.service.Send_MessageService;
import User_Info.websocket.Chat_RoomSocket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static User_Info.websocket.Chat_RoomSocket.ChatMemberSessionMap;

@RestController
public class AdminController {
    @Autowired
    AdminRepository AdminRepository;
    @Autowired
    User_InfoRepository User_InfoRepository;
    @Autowired
    Admin_IssueRepository Admin_IssueRepository;
    @Autowired
    Chat_RoomService Chat_RoomService;
    @Autowired
    Send_MessageService Send_MessageService;
    @Autowired
    AdminService AdminService;
    @Autowired
    EventsRepository EventsRepository;
    @Autowired
    EventsService EventsService;

    @Operation(summary = "Grant tier 2 privileges to an existing admin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Granted tier 2 admin privileges",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User or admin not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/api/admin/grantTier2/{userid}")
    ResponseEntity<?> grantTier2ToExisting(@PathVariable int userid) {
        Optional<User_Info> userO = User_InfoRepository.findById(userid);
        if (userO.isPresent()) {
            User_Info user = userO.get();
            Optional<Admin> adminO = AdminRepository.findById(user.getId());
            if (adminO.isPresent()) {
                Admin admin = adminO.get();
                admin.setAdminTierByInt(2);
                AdminRepository.save(admin);
                return ResponseEntity.ok("The admin associated with userId " + userid + " has been granted tier 2 admin privileges");
            } else {
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The user associated with userId " + userid + " is not an admin.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user associated with userId " + userid + ".");
        }
    }

    @Operation(summary = "Revoke tier 2 status from an existing tier 2 admin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Revoked tier 2 admin privileges",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User or admin not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/api/admin/revokeTier2/{userid}")
    ResponseEntity<?> revokeTier2FromExisting(@PathVariable int userid) {
        Optional<User_Info> userO = User_InfoRepository.findById(userid);
        if (userO.isPresent()) {
            User_Info user = userO.get();
            Optional<Admin> adminO = AdminRepository.findById(user.getId());
            if (adminO.isPresent()) {
                Admin admin = adminO.get();
                if (admin.getTier2Permissions()){
                    admin.setAdminTierByInt(1);
                    AdminRepository.save(admin);
                    return ResponseEntity.ok("The admin associated with userId " + userid + " has had tier 2 admin privileges revoked. They are now Tier 1.");
                } else {
                    return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("This admin does not have Tier 2 privileges.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("The user associated with userId " + userid + " is not an admin.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user associated with userId " + userid + ".");
        }
    }

    // GET isAdmin
    @Operation(summary = "Check is a user is an admin by their userId")
    @GetMapping("/api/is/admin/{id}")
    boolean isAdmin(@PathVariable int id) {
        Optional<User_Info> user = User_InfoRepository.findById(id);
        if (user.isPresent()) {
            Optional<Admin> a = AdminRepository.findById(user.get().getId());
            return a.isPresent();
        } else {
            throw new RuntimeException("User doesn't exist with that id.");
        }
    }

    // GET isT2Admin
    @Operation(summary = "Check is a user is a tier 2 admin by their userId")
    @GetMapping("/api/is/adminT2/{id}")
    boolean isTier2Admin(@PathVariable int id) {
        Optional<User_Info> user = User_InfoRepository.findById(id);
        if (user.isPresent()) {
            Optional<Admin> a = AdminRepository.findById(user.get().getId());
            return a.map(Admin::getTier2Permissions).orElse(false);
        } else {
            throw new RuntimeException("User doesn't exist with that id.");
        }
    }

    // GET Admin
    @Operation(summary = "Get user admin by their userId")
    @GetMapping("/api/admin/{id}")
    Admin accessAdmin(@PathVariable int id) {
        Optional<User_Info> user = User_InfoRepository.findById(id);
        if (user.isPresent()) {
            Optional<Admin> a = AdminRepository.findById(user.get().getId());
            if (a.isEmpty()){
                throw new RuntimeException("This account is not an admin.");
            } else {
                return a.get();
            }
        } else {
            throw new RuntimeException("User doesn't exist with that id.");
        }
    }

    //  GET all admins (User_Info)
    @Operation(summary = "Get all admins as list of User_Info entries")
    @GetMapping("/api/admin/all")
    public List<User_Info> getAllAdmins() {
        List<Admin> admins = AdminRepository.findAll();
        List<User_Info> adminsAsUsers = new ArrayList<User_Info>(admins.size());
        for (Admin a : admins){
            Optional<User_Info> uO = User_InfoRepository.findById(a.getId());
            if (uO.isEmpty()){
                throw new RuntimeException("Something is very wrong with admin feature");
            }
            adminsAsUsers.add(uO.get());
        }
        return adminsAsUsers;
    }

    // POST grant admin
    @Operation(summary = "Grant admin to a user by userid. Grant 0 (Sponsor) or 1 (Tier 1) or 2 (Tier 2) in tier.")
    @PostMapping("/api/grant/admin/{userid}/tier/{tier}")
    public Admin postAdmin(@PathVariable int userid, @PathVariable int tier) {
        Optional<User_Info> userO = User_InfoRepository.findById(userid);

        if (userO.isPresent()) {
            User_Info user = userO.get();
            Optional<Admin> adminO = AdminRepository.findById(user.getId());
            if (adminO.isEmpty()) {
                if (tier == 0){
                    return AdminService.setAdmin(userid, Admin_Tier.SPONSOR);
                }
                if (tier == 1) {
                    return AdminService.setAdmin(userid, Admin_Tier.TIER1);
                }
                if (tier == 2){
                    return AdminService.setAdmin(userid, Admin_Tier.TIER2);
                }
                throw new RuntimeException("Not a tier");
            } else {
                throw new RuntimeException("Already an admin!");
            }
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    // PUT alter existing admin tier
    @PutMapping("/api/admin/alterTier/{userid}/{tier}")
    ResponseEntity<?> alterTierOnExisting(@PathVariable int userid, @PathVariable Admin_Tier tier) {
        Optional<User_Info> userO = User_InfoRepository.findById(userid);
        if (userO.isPresent()) {
            User_Info user = userO.get();
            Optional<Admin> adminO = AdminRepository.findById(user.getId());
            if (adminO.isPresent()) {
                Admin admin = adminO.get();
                admin.setAdminTier(tier);
                AdminRepository.save(admin);
                return ResponseEntity.ok("The admin associated with userId " + userid + " has been granted tier 2 admin privileges");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The user associated with userId " + userid + " is not an admin.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user associated with userId " + userid + ".");
        }
    }

    // DEL remove admin priv
    @Operation(summary = "Revoke all admin privileges from an admin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Revoked all admin privileges",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "User or admin not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("/api/delete/admin/{userId}")
    public ResponseEntity<Object> deleteAdmin(@PathVariable int userId) {
        Optional<User_Info> userO = User_InfoRepository.findById(userId);

        if (userO.isPresent()) {
            User_Info user = userO.get();
            Optional<Admin> adminO = AdminRepository.findById(user.getId());
            if (adminO.isPresent()) {
                user.unassignAdmin();
                Admin admin = adminO.get();
                AdminRepository.delete(admin);
                return ResponseEntity.ok("The user associated with userId " + userId + " has had their admin status revoked");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The user associated with userId " + userId + " is not an admin. Cannot revoke admin status as they do not have status admin.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user associated with userId " + userId + ".");
        }
    }


    // BEGIN ISSUES SECTION OF CONTROLLER
    // More types of Admin Issues may be implemented: delete user not the only issue may be related to Admin_Issue_User, etc.

    // VIEW ISSUES
    @Operation(summary = "Get all admin issues")
    @GetMapping("api/adminIssue/all")
    public List<Admin_Issue> getAllIssues(){
        return Admin_IssueRepository.findAll();
    }

    @Operation(summary = "Get all open admin issues")
    @GetMapping("api/adminIssue/open/all")
    public List<Admin_Issue> getAllOpenIssues(){
        return Admin_IssueRepository.findAllByResolved(false);
    }

    @Operation(summary = "Get all closed admin issues")
    @GetMapping("api/adminIssue/closed/all")
    public List<Admin_Issue> getAllClosedIssues(){
        return Admin_IssueRepository.findAllByResolved(true);
    }


    // APPROVE OR DENY ANY TYPE OF ISSUE
    @Operation(summary = "Approve (status=true) or deny (status=false) an admin issue to resolve it. Requires tier 2 admin access.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resolved issue",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Admin lacks required permissions",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/api/adminIssue/close/{status}/{adminIssueId}/{adminId}")
    public ResponseEntity<?> closeIssueApproveOrDeny(@PathVariable boolean status, @PathVariable long adminIssueId, @PathVariable int adminId){
        Admin admin = AdminRepository.findById(adminId).orElseThrow(RuntimeException::new);
        if (!admin.getTier2Permissions()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The admin associated with adminId " + adminId + " lacks the required privileges for this operation.");
        }
        Admin_Issue adminIssue = Admin_IssueRepository.findById(adminIssueId).orElseThrow(RuntimeException::new);
        if (status){
            adminIssue.setStatus(Issue_Status_Type.APPROVED);
            if (adminIssue.getType() == Issue_Type.EVENTSISSUE){
                AdminEventRequest req = adminIssue.getEventRequest();
                Events e = new Events();
                ResponseEntity<Events> resp = EventsService.createEventFromRequest(req.getSponsorId(), e);
                if (resp.getStatusCode() != HttpStatus.CREATED || resp.getBody() == null) {
                    throw new RuntimeException("Event creation failed");
                }
                Events created = resp.getBody();
                created.setEventName(req.eventName);
                created.setEventLocation(req.eventLocation);
                created.setEventDate(req.eventDate);
                created.setEventTime(req.eventTime);
//                created.setEventDescription(req.description);
//                created.setEventSponsorName(req.sponsorName);
            }
            else if (adminIssue.getType() == Issue_Type.USERISSUE){
                Admin_Issue_User delUserIssue = (Admin_Issue_User) adminIssue;
                Integer terminate = delUserIssue.getProposedUserId();
                if (terminate == null) {
                    throw new RuntimeException("User deletion issue missing proposedUserId in AdminService - contact Backend");
                }
                User_InfoRepository.deleteById(terminate);
            }
            adminIssue.setResolved(true);
            Admin_IssueRepository.save(adminIssue);
            return ResponseEntity.ok("The issue has been approved and resolved.");
        } else {
            adminIssue.setStatus(Issue_Status_Type.DENIED);
            adminIssue.setResolved(true);
            Admin_IssueRepository.save(adminIssue);
            return ResponseEntity.ok("The issue has been denied and resolved.");
        }
    }




    // OPEN ISSUES

    // issue official event approval (creation of events officially managed by/associated with ISU requires ISU authorization)

//    @Operation(summary = "Open a new admin issue 'Approve Event' without an admin assigned to the issue")
//    @PostMapping("/api/adminIssue/approveEvent/{eventId}")
//    public Admin_Issue openIssueApproveEvent(@PathVariable int eventId, @RequestBody(required = false) String desc){
//        return AdminService.openIssueApproveEvent(eventId, desc);
//    }
//    @Operation(summary = "Open a new admin issue 'Approve Event' with an admin assigned to the issue")
//    @PostMapping("/api/adminIssue/approveEvent/{eventId}/assignAdmin/{adminId}")
//    public Admin_Issue openIssueApproveEventWithAssignAdmin(@PathVariable int eventId, @PathVariable int adminId, @RequestBody(required = false) String desc){
//        return AdminService.openIssueApproveEvent(eventId, adminId, desc);
//    }

    @Operation(summary = "Open a new admin issue 'Approve Event' without an admin assigned to the issue")
    @PostMapping("/api/adminIssue/approveEvent")
    public Admin_Issue openIssueApproveEvent(@RequestBody AdminEventRequest event){
        Admin_Issue issue = AdminService.openIssueApproveEvent(event);
        if (issue == null){
            throw new RuntimeException("sponsorId must correspond to a user with Admin.SPONSOR status");
        }
        return issue;
    }
    @Operation(summary = "Open a new admin issue 'Approve Event' with an admin assigned to the issue")
    @PostMapping("/api/adminIssue/approveEvent/assignAdmin/{adminId}")
    public Admin_Issue openIssueApproveEventWithAssignAdmin(@PathVariable int adminId, @RequestBody AdminEventRequest event){
        Admin_Issue issue = AdminService.openIssueApproveEvent(event, adminId);
        if (issue == null){
            throw new RuntimeException("sponsorId must correspond to a user with Admin.SPONSOR status");
        }
        return issue;
    }


    // issue delete user (delete requires ISU authorization)
    @Operation(summary = "Open a new admin issue 'Delete User' without an admin assigned to the issue")
    @PostMapping("/api/adminIssue/deleteUser/{userId}")
    public Admin_Issue openIssueDeleteUser(@PathVariable int userId, @RequestBody(required = false) String desc){
        return AdminService.openIssueDeleteUser(userId, desc);
    }
    @Operation(summary = "Open a new admin issue 'Approve Event' with an admin assigned to the issue")
    @PostMapping("/api/adminIssue/deleteUser/{userId}/assignAdmin/{adminId}")
    public Admin_Issue openIssueDeleteUserWithAssignAdmin(@PathVariable int userId, @PathVariable int adminId, @RequestBody(required = false) String desc){
        return AdminService.openIssueDeleteUser(userId, adminId, desc);
    }


    // open a miscellaneous
//    @Operation(summary = "Open a new admin issue 'Miscellaneous' without an admin assigned to the issue")
//    @PostMapping("/api/adminIssue/miscellaneous")
//    public Admin_Issue openIssueMiscellaneous(@RequestBody(required = false) String desc){
//        return AdminService.openIssueMiscellaneous(desc);
//    }
//    @Operation(summary = "Open a new admin issue 'Miscellaneous' with an admin assigned to the issue")
//    @PostMapping("/api/adminIssue/miscellaneous/assignAdmin/{adminId}")
//    public Admin_Issue openIssueMiscellaneousWithAssignAdmin(@PathVariable int adminId, @RequestBody(required = false) String desc){
//        return AdminService.openIssueMiscellaneous(adminId, desc);
//    }


    // BEGIN ADMIN SPECIFIC CHAT_ROOM OPERATIONS
    @Operation(summary = "Admin get all public chatrooms")
    @GetMapping("/api/admin/allPublicChatrooms")
    public List<? extends Chat_Room> getAllPublicChatrooms(){
        return Chat_RoomService.getAllPublicChatrooms();
    }

    @Operation(summary = "Admin send message to multiple chatrooms in AdminMessageRequest JSON Body format")
    @PostMapping("/api/admin/{adminId}/sendto")
        public ResponseEntity<String> sendMessageToChatrooms(@PathVariable int adminId, @RequestBody AdminMessageRequest msgRequest){
        Logger logger = LoggerFactory.getLogger(Chat_RoomSocket.class);
        User_Info admin = User_InfoRepository.findById(adminId).orElseThrow(RuntimeException::new);

        Send_MessageService.broadcastToMultRmS(msgRequest.getToRooms(), admin, msgRequest.getMsg(), ChatMemberSessionMap, logger);
        return ResponseEntity.ok("Message " + msgRequest.getMsg() + " sent to rooms: " + msgRequest.getToRooms().toString());
    }
}