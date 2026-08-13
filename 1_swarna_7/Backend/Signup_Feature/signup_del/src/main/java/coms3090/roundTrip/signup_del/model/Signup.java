package coms3090.roundTrip.signup_del.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * @author tanya
 * Model for Signup
 */
@Entity
public class Signup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // For coordination with the DB; unique ID for each user
    private String userName;
    private String password;
    private String email;
    private String  phoneNum;

    // Constructor
    public Signup() {
        //fill
    }

    // Setters and getters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
