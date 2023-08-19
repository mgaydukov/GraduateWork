package com.project.chip.controllers;

import com.project.chip.models.User;
import com.project.chip.models.Video;
import com.project.chip.repos.VideoRepo;
import com.project.chip.services.SolutionService;
import com.project.chip.services.openai.FormInputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class SolutionController {
    @Autowired private VideoRepo videoRepo;
    @Autowired private SolutionService solutionService;

    @GetMapping("/video/{video}/solution")
    public String answer(@PathVariable Video video, Model model){
        if (!videoRepo.existsById(video.getId()))
            return "redirect:/main";

        Optional<Video> videoTmp = videoRepo.findById(video.getId());
        ArrayList<Video> list = new ArrayList<>();
        videoTmp.ifPresent(list::add);
        model.addAttribute("video", list);
        model.addAttribute("title", "Решение");

        return "solution";
    }

    @PostMapping("video/{video}/solution")
    public String chat(@PathVariable Video video,
                       @AuthenticationPrincipal User user,
                       @ModelAttribute FormInputDTO dto,
                       Model model) {
        model.addAttribute("title", "Решение");
        Video videoT = videoRepo.findById(video.getId()).orElseThrow();
        Optional<Video> videotmp = videoRepo.findById(video.getId());
        ArrayList<Video> list = new ArrayList<>();
        videotmp.ifPresent(list::add);
        model.addAttribute("video", list);
        model.addAttribute("response", solutionService.solve(video, user.getId(), video.getId(), dto.prompt()));
        return "solution";
    }
}
