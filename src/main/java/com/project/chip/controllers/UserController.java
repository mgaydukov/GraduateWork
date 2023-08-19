package com.project.chip.controllers;

import com.project.chip.models.User;
import com.project.chip.models.Video;
import com.project.chip.repos.UserRepo;
import com.project.chip.repos.VideoRepo;
import com.project.chip.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired private UserRepo userRepo;
    @Autowired private UserService userService;
    @Autowired private VideoRepo videoRepo;

    @GetMapping("{user}")
    public  String userProfile(
            @PathVariable User user,
            Model model){
        if (!userRepo.existsById(user.getId()))
            return "redirect:/main";

        Optional<User> userInfo = userRepo.findById(user.getId());
        ArrayList<User> list = new ArrayList<>();
        userInfo.ifPresent(list::add);
        model.addAttribute("userInfo", list);
        Iterable<Video> videos = videoRepo.findByPublisher(user);
        model.addAttribute("videos", videos);
        model.addAttribute("title", "Профиль");

        return "profile";
    }

    @GetMapping("edit")
    public String userProfileEdit(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("username", user.getUsername());
        model.addAttribute("bio", user.getBio());
        model.addAttribute("profilePic", user.getProfilePic());
        model.addAttribute("password", user.getPassword());
        return "profile-edit";
    }

    @PostMapping("edit")
    public String userEdit(
            @AuthenticationPrincipal User user,
            @RequestParam String username,
            @RequestParam String bio,
            @RequestParam String profilePic,
            @RequestParam String password
    ){
        userService.updateProfile(user, username, bio, profilePic, password);
        return "redirect:/user/" + user.getId();
    }

    @PostMapping("edit/delete")
    public String userdelete(
            @AuthenticationPrincipal User user
    ){
        userService.deleteUser(user);
        return "redirect:/login";
    }
}
