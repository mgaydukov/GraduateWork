package com.project.chip.controllers;

import com.project.chip.models.User;
import com.project.chip.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizationController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user,
                          @RequestParam String confirm,
                          Model model) {

        if(!userService.addUser(user)){
            model.addAttribute("errorMessage", "Пользователь с таким именем уже существует!");
            return "registration";
        }
        return "redirect:/login ";
    }
}
