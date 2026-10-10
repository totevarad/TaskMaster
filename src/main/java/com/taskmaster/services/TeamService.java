package com.taskmaster.services;


import com.taskmaster.exceptions.ResourceNotFoundException;
import com.taskmaster.models.Team;
import com.taskmaster.models.TeamMember;
import com.taskmaster.models.User;
import com.taskmaster.models.dto.TeamRequest;
import com.taskmaster.models.dto.TeamResponse;
import com.taskmaster.repositories.TeamMemberRepository;
import com.taskmaster.repositories.TeamRepository;
import com.taskmaster.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    public TeamResponse createTeam(TeamRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Team team = new Team();
        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setOwner(owner);

        Team savedTeam = teamRepository.save(team);

        // Add owner as a member
        TeamMember ownerMember = new TeamMember();
        ownerMember.setTeam(savedTeam);
        ownerMember.setUser(owner);
        ownerMember.setRoleInTeam("OWNER");
        teamMemberRepository.save(ownerMember);

        return mapToResponse(savedTeam);
    }

    public List<TeamResponse> getMyTeams() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return teamMemberRepository.findByUserId(user.getId()).stream()
                .map(member -> mapToResponse(member.getTeam()))
                .collect(Collectors.toList());
    }

    public void addMemberToTeam(String teamId, String userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (teamMemberRepository.findByTeamIdAndUserId(teamId, userId).isPresent()) {
            throw new IllegalArgumentException("User is already a member of this team");
        }

        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setUser(user);
        member.setRoleInTeam("MEMBER");
        teamMemberRepository.save(member);
    }

    private TeamResponse mapToResponse(Team team) {
        TeamResponse response = new TeamResponse();
        response.setId(team.getId());
        response.setName(team.getName());
        response.setDescription(team.getDescription());
        response.setOwnerId(team.getOwner().getId());
        return response;
    }
}
