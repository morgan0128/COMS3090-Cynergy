package User_Info.model;

import User_Info.service.AdminService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

// This is NOT a table.
public class AdminEventRequest {
//    "eventName": "ISU AfterDark",
//    "eventLocation": "Memorial Union",
//    "eventDate": "2025-10-10",
//    "eventTime": "11:56:00",
//    "sponsorId": 1

    @NotNull(message = "sponsorName is required")
    public String sponsorName;

    @NotNull(message = "eventName is required")
    public String eventName;

    public String eventLocation;

    public String description;

    public LocalDate eventDate;

    public LocalTime eventTime;

    @NotNull(message = "sponsorId (aka userId associated with the sponsor's account) is required")
    public Integer sponsorId;


    public AdminEventRequest(){

    }

    public Integer getSponsorId() { return sponsorId; }

    public void setSponsorId(Integer sponsorId) {
        this.sponsorId = sponsorId;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalTime eventTime) {
        this.eventTime = eventTime;
    }
}
