package com.example.synergy.items;

public final class TicketApi {

    private static final String BASE = "http://coms-3090-016.class.las.iastate.edu:8080";

    // From Admin Ticket Approval writeup
    public static final String POST_APPROVE_EVENT = BASE + "/api/adminIssue/approveEvent";
    public static final String POST_DELETE_USER_NO_ADMIN = BASE + "/api/adminIssue/deleteUser/%d";

    private TicketApi() {
        // no instances
    }
}
