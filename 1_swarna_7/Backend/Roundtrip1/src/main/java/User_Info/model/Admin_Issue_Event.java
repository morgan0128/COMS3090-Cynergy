package User_Info.model;

import User_Info.enumerator.Issue_Type;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@DiscriminatorValue("EVENT")
public class Admin_Issue_Event extends Admin_Issue {

    private String sponsorName;
    private String proposedEventName;
    private String proposedEventLocation;
    private String proposedDescription;
    private LocalDate proposedEventDate;
    private LocalTime proposedEventTime;

    private Integer proposedSponsorId;

    public Admin_Issue_Event() {
        super();
        this.setType(Issue_Type.EVENTSISSUE);
    }

    public Admin_Issue_Event(AdminEventRequest req, Admin a) {
        super(a);
        this.setType(Issue_Type.EVENTSISSUE);
        applyRequest(req);
    }

    public Admin_Issue_Event(AdminEventRequest req) {
        super();
        this.setType(Issue_Type.EVENTSISSUE);
        applyRequest(req);
    }

    private void applyRequest(AdminEventRequest req) {
        if (req == null) return;
        this.sponsorName = req.sponsorName;
        this.proposedEventName = req.eventName;
        this.proposedEventLocation = req.eventLocation;
        this.proposedDescription = req.description;
        this.proposedEventDate = req.eventDate;
        this.proposedEventTime = req.eventTime;
        this.proposedSponsorId = req.sponsorId;
    }

    @Override
    @Transient
    public AdminEventRequest getEventRequest() {
        AdminEventRequest req = new AdminEventRequest();
        req.eventName = this.proposedEventName;
        req.eventLocation = this.proposedEventLocation;
        req.eventDate = this.proposedEventDate;
        req.eventTime = this.proposedEventTime;
        req.sponsorId = this.proposedSponsorId;
        return req;
    }

    public Integer getProposedSponsorId() { return proposedSponsorId; }
    public void setProposedSponsorId(Integer proposedSponsorId) { this.proposedSponsorId = proposedSponsorId;}
    public String getProposedEventName() { return proposedEventName; }
    public void setProposedEventName(String proposedEventName) { this.proposedEventName = proposedEventName; }

    public String getProposedEventLocation() { return proposedEventLocation; }
    public void setProposedEventLocation(String proposedEventLocation) { this.proposedEventLocation = proposedEventLocation; }

    public LocalDate getProposedEventDate() { return proposedEventDate; }
    public void setProposedEventDate(LocalDate proposedEventDate) { this.proposedEventDate = proposedEventDate; }

    public LocalTime getProposedEventTime() { return proposedEventTime; }
    public void setProposedEventTime(LocalTime proposedEventTime) { this.proposedEventTime = proposedEventTime; }
}
