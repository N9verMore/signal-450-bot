package org.atics.bot450.message;

import lombok.Data;

import java.util.List;

@Data
public class GroupPayload {
    private String name;
    private String description;
    private String id;
    private String internal_id;
    private List<String> members;
    private boolean blocked;
    private List<String> pending_invites;
    private List<String> pending_requests;
    private String invite_link;
    private List<String> admins;
}
