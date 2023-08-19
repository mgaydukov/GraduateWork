package com.project.chip.controllers;

import com.project.chip.models.Video;
import com.project.chip.repos.VideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubjectsController {

    @Autowired
    private VideoRepo videoRepo;

    @GetMapping("/language")
    public String video(Model model){
        Iterable<Video> videos = videoRepo.findBySubject("language");
        model.addAttribute("videos", videos);
        model.addAttribute("title", "Иностранные языки");
        return "language";
    }

    @GetMapping("/economics")
    public String economics(Model model){
        Iterable<Video> videos = videoRepo.findBySubject("economics");
        model.addAttribute("videos", videos);
        model.addAttribute("title", "Экономика");
        return "economics";
    }

    @GetMapping("/history")
    public String history(Model model){
        Iterable<Video> videos = videoRepo.findBySubject("history");
        model.addAttribute("videos", videos);
        model.addAttribute("title", "История");
        return "history";
    }

    @GetMapping("/other")
    public String other(Model model){
        Iterable<Video> videos = videoRepo.findBySubject("other");
        model.addAttribute("videos", videos);
        model.addAttribute("title", "Другие предметы");
        return "other";
    }
}
