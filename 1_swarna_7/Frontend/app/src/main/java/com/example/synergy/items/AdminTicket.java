// package: com.example.synergy.items
package com.example.synergy.items;

public class AdminTicket {

    private final int issueId;
    private final String type;          // USERISSUE / EVENTSISSUE / etc.
    private String status;              // PENDING / APPROVED / DENIED / ...
    private final boolean resolved;
    private final String description;
    private final int proposedUserId;
    private final String proposedUsername;

    public AdminTicket(int issueId,
                       String type,
                       String status,
                       boolean resolved,
                       String description,
                       int proposedUserId,
                       String proposedUsername) {
        this.issueId = issueId;
        this.type = type;
        this.status = status;
        this.resolved = resolved;
        this.description = description;
        this.proposedUserId = proposedUserId;
        this.proposedUsername = proposedUsername;
    }

    public int getIssueId() {
        return issueId;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status != null ? status : "";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isResolved() {
        return resolved;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public int getProposedUserId() {
        return proposedUserId;
    }

    public String getProposedUsername() {
        return proposedUsername != null ? proposedUsername : "";
    }

    // Treat "PENDING" as OPEN
    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(getStatus());
    }
}

