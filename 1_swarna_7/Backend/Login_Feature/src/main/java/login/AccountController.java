package login;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {
    @Autowired
    AccountRepository AccountRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";
    private String loginFailure = "{\"message\":\"Failed to login: Incorrect password.\"}";

    // Currently, essentially a "change password" method; no other Account columns but email_id and user_password
    // Don't want change email feature for now, but would be easy to implement here (by being careful
    // before calling "save()"...)
    @PutMapping("/Accounts/{id}")
    Account editUser(@PathVariable String id, @RequestBody Account request) {
        Account account = AccountRepository.findByEmailId(id);

        if (account == null) {
            throw new RuntimeException("Account id does not exist");
        }
        if (!account.getEmailId().equals(id)) {
            throw new RuntimeException("Account id does not match Account request id");
        }

        // Stops "persist" method from being called in testAccountRepository.save(request);,
        // which would create a new account in the table
        if (!request.getEmailId().equals(id)) {
            throw new RuntimeException("You cannot change your email!");
        }
        AccountRepository.save(request);
        return AccountRepository.findByEmailId(id);
    }

    @GetMapping("/Accounts/Login/{id}")
    String userLogin(@PathVariable String id, @RequestBody Account request) {
        Account account = AccountRepository.findByEmailId(id);
        if (account == null) {
            throw new RuntimeException("Account id does not exist");
        }
        if (!account.getEmailId().equals(request.getEmailId())) {
            throw new RuntimeException("Account id does not match Account request id");
        }
        // If we've reached here in this method, then the Mapping email matches the Request account's email
        if (account.getUserPassword().equals(request.getUserPassword())) {
            return success;
        } else {
            return loginFailure;
        }
    }

}
