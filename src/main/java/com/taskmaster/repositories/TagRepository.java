package com.taskmaster.repositories;

import com.taskmaster.models.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface TagRepository extends MongoRepository<Tag, String> {
    Optional<Tag> findByName(String name);
}
