package com.project.chip.services;

import com.project.chip.models.User;
import com.project.chip.models.Video;
import com.project.chip.repos.VideoRepo;
import com.project.chip.services.speechmatics.SpeechmaticsAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoService {
    @Autowired
    private VideoRepo videoRepo;

    public void updateVideo(Video video, String name, String description){
        if (!name.isEmpty())
            video.setName(name);

        if (!description.isEmpty())
            video.setDescription(description);

       videoRepo.save(video);
    }

    public void updateVideo(Video video, String name, String description, String fullText){
        if (!name.isEmpty())
            video.setName(name);

        if (!description.isEmpty())
            video.setDescription(description);

        if (!fullText.isEmpty())
            video.setFullText(fullText);

        videoRepo.save(video);
    }
    public void updateVideo(Video video, String fullText) {
        if (!fullText.isEmpty())
            video.setName(fullText);

        videoRepo.save(video);
    }

    public void addVideo(String name, String link, User user, String subject, String description) {
        Video video = new Video(name, link, user, subject, description);
        SpeechmaticsAPI speechmaticsAPI = new SpeechmaticsAPI(video.getLink());
        video.setFullText(speechmaticsAPI.transcribe());
        videoRepo.save(video);
    }

    public void deleteVideo(Video video) {
        videoRepo.delete(video);
    }
}
