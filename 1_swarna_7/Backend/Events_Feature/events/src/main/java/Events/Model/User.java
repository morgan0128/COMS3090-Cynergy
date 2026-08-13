package Events.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Entity
@Table(name = "user_info")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    private String emailId;
    private String phoneNum;
    private String userName;
    private String userPassword;

    // One user can have many events
    // Included cascade to delete ALL owned events if user is deleted
    // orphanRemoval ensures event (child) is deleted from db when removed from user (parent) collection

    @JsonManagedReference
    @OneToMany (mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Events> events;

    public String getEmailId() { return emailId; }
    void setEmailId() { this.emailId = emailId; }

    public String getPhoneNum() { return phoneNum; }
    void setPhoneNum() { this.phoneNum = phoneNum; }

    public String getUserName() {return userName; }
    void setUserName() {this.userName = userName; }

    public String getUserPassword() { return userPassword; }
    void setUserPassword () { this.userPassword = userPassword; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

}
