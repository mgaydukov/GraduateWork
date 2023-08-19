package com.project.chip.controllers;

import com.project.chip.models.Video;
import com.project.chip.repos.VideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @Autowired private VideoRepo videoRepo;

    @GetMapping("/")
    public String index(Model model){
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String main(Model model){
        model.addAttribute("title", "Главная");
        Iterable<Video> videos = videoRepo.findAll();
        model.addAttribute("videos", videos);
        return "main";
    }

    @GetMapping("/usermanual")
    public String usermanual(Model model){
        model.addAttribute("title", "Руководство пользователя");
        return "usermanual";
    }
}
