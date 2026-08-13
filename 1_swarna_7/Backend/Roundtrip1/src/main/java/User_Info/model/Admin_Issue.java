package User_Info.model;

import User_Info.enumerator.Issue_Status_Type;
import User_Info.enumerator.Issue_Type;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "issue_kind")
public abstract class Admin_Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issue_id;

    @JsonBackReference("openedByAdmin")
    @ManyToOne
    @JoinColumn(name = "opened_by_admin", nullable = true)
    private Admin admin;

    @JsonBackReference("closedByAdmin")
    @OneToOne
    @JoinColumn(name = "approved_by_admin", nullable = true)
    private Admin adminApproved;

    @Enumerated(EnumType.STRING)
    private Issue_Type type;

    @Enumerated(EnumType.STRING)
    private Issue_Status_Type status;

    private boolean resolved;

    @Lob
    private String description;

    public Admin_Issue() {
        this.type = Issue_Type.MISCELLANEOUS;
        this.status = Issue_Status_Type.PENDING;
        this.resolved = false;
    }

    public Admin_Issue(Admin a) {
        this.admin = a;
        this.type = Issue_Type.MISCELLANEOUS;
        this.status = Issue_Status_Type.PENDING;
        this.resolved = false;
    }

    @Transient
    public AdminEventRequest getEventRequest() {
        throw new UnsupportedOperationException("Not an event issue");
    }

    public Long getIssue_id() { return issue_id; }
    public void setIssue_id(Long issue_id) { this.issue_id = issue_id; }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }

    public Admin getAdminApproved() { return adminApproved; }
    public void setAdminApproved(Admin adminApproved) { this.adminApproved = adminApproved; }

    public Issue_Type getType() { return type; }
    public void setType(Issue_Type type) { this.type = type; }

    public boolean getResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    public Issue_Status_Type getStatus() { return status; }
    public void setStatus(Issue_Status_Type s) { this.status = s; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
