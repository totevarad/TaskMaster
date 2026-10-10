package com.taskmaster.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Document

public class TeamMember {

    @Id
    
    private String id;

    @DBRef
    
    private Team team;

    @DBRef
    
    private User user;

    
    private String roleInTeam; // e.g. "MEMBER", "ADMIN"

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getRoleInTeam() { return roleInTeam; }
    public void setRoleInTeam(String roleInTeam) { this.roleInTeam = roleInTeam; }
}
