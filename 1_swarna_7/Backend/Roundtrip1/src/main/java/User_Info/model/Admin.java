package User_Info.model;

import User_Info.enumerator.Admin_Tier;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Admin {

    @Id
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User_Info user_info;

    @Enumerated(EnumType.STRING)
    @Column
    private Admin_Tier adminTier;

    @JsonManagedReference("openedByAdmin")
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Admin_Issue> adminIssues = new ArrayList<>();

    public Admin() {}

    public Admin(User_Info u){
        this.user_info = u;
    }

    public int getId() { return id; }

    public Admin_Tier getAdminTier() { return adminTier; }

    public void setAdminTierByInt(int t) {
        switch(t){
            case 0 -> this.adminTier = Admin_Tier.SPONSOR;
            case 1 -> this.adminTier = Admin_Tier.TIER1;
            case 2 -> this.adminTier = Admin_Tier.TIER2;
        }
    }

    public void setAdminTier(Admin_Tier t) { this.adminTier = t; }

    public boolean getTier2Permissions() {
        return this.adminTier == Admin_Tier.TIER2;
    }

    public List<Admin_Issue> getAdminIssues() { return adminIssues; }

    public void setAdminIssues(List<Admin_Issue> adminIssues) {
        this.adminIssues = adminIssues;
    }

    public void addToAdminIssues(Admin_Issue adminIssue){
        this.adminIssues.add(adminIssue);
        adminIssue.setAdmin(this);
    }
}
