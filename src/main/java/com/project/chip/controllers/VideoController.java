package com.project.chip.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chip.models.User;
import com.project.chip.models.Video;
import com.project.chip.repos.VideoRepo;
import com.project.chip.services.VideoService;
import com.project.chip.services.openai.OpenAiApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequestMapping("/video")
public class VideoController {
    @Autowired private VideoRepo videoRepo;
    @Autowired private VideoService videoService;
    @Autowired private ObjectMapper jsonMapper;
    @Autowired private OpenAiApiClient client;

    @GetMapping("/")
    public String videoRedirect(Model model){
        return "redirect:/main";
    }

    @GetMapping("add")
    public String videoAdd(Model model){
        model.addAttribute("title", "Добавление видео");
        return "video-add";
    }

    @PostMapping("add")
    public String videoPostAdd
            (       @AuthenticationPrincipal User user,
                    @RequestParam String name,
                    @RequestParam String link,
                    @RequestParam String subject,
                    @RequestParam String description){
        videoService.addVideo(name, link, user, subject, description);
        return "redirect:/main";
    }

    @GetMapping("{video}")
    public String videoDetail(@PathVariable Video video,
                              @AuthenticationPrincipal User user,
                              Model model){
        if (!videoRepo.existsById(video.getId()))
            return "redirect:/main";

        Optional<Video> videoT = videoRepo.findById(video.getId());
        model.addAttribute("title", "Видео " + videoT.get().getName());
        ArrayList<Video> list = new ArrayList<>();
        videoT.ifPresent(list::add);
        model.addAttribute("video", list);
        model.addAttribute("user", user);
        model.addAttribute("publisher", videoT.get().getPublisherName());
        model.addAttribute("publisherId", videoT.get().getPublisher().getId());

        return "video";
    }

    @GetMapping("{video}/edit")
    public String videoEditGet(
            @PathVariable Video video,
            @AuthenticationPrincipal User user,
            Model model
    ){
        if (video.getPublisher().getId().equals(user.getId()) ||
                user.isAdmin()){
            model.addAttribute("title", "Редактирование видео");
            model.addAttribute("video", video);
            return "video-edit";
        }
        return "redirect:/main";
    }

    @PostMapping("{video}/edit")
    public String videoEdit(
            @PathVariable Video video,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String fullText,
            @RequestParam("videoId") Video videoT,
            Model model
    ){
        videoService.updateVideo(videoT, name, description, fullText);
        model.addAttribute("title", "Редактирование видео");
        return "redirect:/video/{video}";
    }

    @PostMapping("{video}/edit/delete")
    public String videoDelete(
            @PathVariable Video video,
            @RequestParam("videoId") Video videoT
    ){
        videoService.deleteVideo(videoT);
        return "redirect:/main";
    }
}
