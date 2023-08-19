package com.project.chip.repos;

import com.project.chip.models.Solution;
import com.project.chip.models.Video;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SolutionRepo extends CrudRepository<Solution, Long> {
    List<Video> findByVideoId(Long videoId);
    List<Video> findByUserId(String userId);
}