package com.project.chip.repos;

import com.project.chip.models.User;
import com.project.chip.models.Video;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepo extends CrudRepository<Video, Long> {
    List<Video> findBySubject(String subject);
    List<Video> findByPublisher(User publisher);
    List<Video> findByLink(String link);
}
