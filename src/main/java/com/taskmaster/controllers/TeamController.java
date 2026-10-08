package com.taskmaster.controllers;

import com.taskmaster.models.dto.TeamRequest;
import com.taskmaster.models.dto.TeamResponse;
import com.taskmaster.services.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest request) {
        return new ResponseEntity<>(teamService.createTeam(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getMyTeams() {
        return ResponseEntity.ok(teamService.getMyTeams());
    }

    @PostMapping("/{teamId}/members/{userId}")
    public ResponseEntity<Void> addMemberToTeam(@PathVariable String teamId, @PathVariable String userId) {
        teamService.addMemberToTeam(teamId, userId);
        return ResponseEntity.ok().build();
    }
}
