package User_Info.model;

import User_Info.enumerator.Issue_Type;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("USER")
public class Admin_Issue_User extends Admin_Issue {

    @Column(name = "user_id")
    private Integer proposedUserId;

    private String proposedUsername;

    public Admin_Issue_User() {
        super();
        this.setType(Issue_Type.USERISSUE);
    }

    public Admin_Issue_User(User_Info u, Admin a) {
        super(a);
        this.setType(Issue_Type.USERISSUE);
        applyUser(u);
    }

    public Admin_Issue_User(User_Info u) {
        super();
        this.setType(Issue_Type.USERISSUE);
        applyUser(u);
    }

    @Override
    @Transient
    public AdminEventRequest getEventRequest() {
        return null;
    }

    private void applyUser(User_Info u) {
        if (u == null) return;
        this.proposedUserId = u.getId();
        this.proposedUsername = u.getUserName();
    }



    @Transient
    public User_Info getUser_info() {
        if (proposedUserId == null){
            return null;
        }
        User_Info u = new User_Info();
        u.setId(proposedUserId);
        if (proposedUsername != null) {
            u.setUserName(proposedUsername);
        }
        return u;
    }

    public void setUser_info(User_Info user_info) {
        applyUser(user_info);
    }

    public Integer getProposedUserId(){return proposedUserId;}
    public void setProposedUserId(Integer proposedUserId){this.proposedUserId = proposedUserId;}
    public String getProposedUsername() {return proposedUsername;}
    public void setProposedUsername(String proposedUsername) {this.proposedUsername = proposedUsername;}
}
