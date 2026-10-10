package com.taskmaster.repositories;

import com.taskmaster.models.TeamMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends MongoRepository<TeamMember, String> {
    List<TeamMember> findByTeamId(String teamId);
    List<TeamMember> findByUserId(String userId);
    Optional<TeamMember> findByTeamIdAndUserId(String teamId, String userId);
}
