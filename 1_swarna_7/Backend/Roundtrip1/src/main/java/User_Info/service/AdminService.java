package User_Info.service;

import User_Info.enumerator.Admin_Tier;
import User_Info.model.*;
import User_Info.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    User_InfoRepository User_InfoRepository;

    @Autowired
    AdminRepository AdminRepository;

    @Autowired
    EventsRepository EventsRepository;

    @Autowired
    Admin_IssueRepository Admin_IssueRepository;

    @Autowired
    Admin_Issue_EventRepository Admin_Issue_EventRepository;

    @Autowired
    Admin_Issue_UserRepository Admin_Issue_UserRepository;

    public Admin setAdmin(int userid, Admin_Tier t) {
        Optional<User_Info> uO = User_InfoRepository.findById(userid);
        if (uO.isEmpty()) {
            throw new RuntimeException("Account doesn't exist with that email.");
        }
        User_Info u = uO.get();
        Admin admin = new Admin(u);
        admin.setAdminTier(t);
        return AdminRepository.save(admin);
    }


    public Admin_Issue openIssueApproveEvent(AdminEventRequest e){
        return openIssueApproveEvent(e, -1);
    }

    // The AdminEventRequest.sponsorId must be a valid Admin of Tier SPONSOR! if not, return null
    public Admin_Issue openIssueApproveEvent(AdminEventRequest e, int adminId){
        if (!validateAdminEventRequestSponsor(e)){
            return null;
        }
        Admin_Issue_Event newIssue;
        if (adminId == -1){
            newIssue = new Admin_Issue_Event(e);
        } else {
            Admin admin = AdminRepository.findById(adminId).orElseThrow(()-> new RuntimeException("admin not found by id"));
            newIssue = new Admin_Issue_Event(e, admin);
        }
        return Admin_Issue_EventRepository.save(newIssue);
    }



    public Admin_Issue openIssueDeleteUser(int userId, String description){
        return openIssueDeleteUser(userId, -1, description);
    }

    public Admin_Issue openIssueDeleteUser(int userId, int adminId, String description){
        User_Info user = User_InfoRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Admin_Issue_User issue;
        if (!(adminId == -1 || AdminRepository.findById(adminId).isEmpty())){
            issue = new Admin_Issue_User(user, AdminRepository.findById(adminId).get());
        } else {
            issue = new Admin_Issue_User(user);
        }
        issue.setDescription(description);
        issue.setResolved(false);

        return Admin_IssueRepository.save(issue);
    }

    public boolean validateAdminEventRequestSponsor(AdminEventRequest a){
        try {
            return AdminRepository.findById(a.getSponsorId()).orElseThrow().getAdminTier() == Admin_Tier.SPONSOR;
        } catch (NoSuchElementException e){
            return false;
        }

    }

//    public Admin_Issue openIssueMiscellaneous(String description){
//        return openIssueMiscellaneous(-1, description);
//    }
//
//    public Admin_Issue openIssueMiscellaneous(int adminId, String description){
//        Admin_Issue newIssue;
//        if (adminId == -1){
//            newIssue = new Admin_Issue();
//            newIssue.setDescription(description);
//        } else {
//            Admin admin = AdminRepository.findById(adminId).orElseThrow(()-> new RuntimeException("admin not found by id"));
//            newIssue = new Admin_Issue(admin);
//            newIssue.setDescription(description);
//        }
//        return Admin_IssueRepository.save(newIssue);
//    }


}
