package login;

//import org.springframework.boot.*;
//import com.h2database.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
//import jakarta.persistence.OneToOne;

//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.Pattern;


@Entity
//@Getter
//@Setter
public class Account {

    @Id
    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    private String emailId;

    private String userPassword;


    public Account(String n, String p){
        this.emailId = n;
        this.userPassword = p;
    }

    public Account(){

    }

    public String getEmailId() {
        return emailId;
    }

    public String getUserPassword() {
        return userPassword;
    }
}

