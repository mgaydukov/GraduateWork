package com.project.chip.controllers;

import com.project.chip.models.Role;
import com.project.chip.models.User;
import com.project.chip.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("userlist")
    public String userList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user-list";
    }

    @GetMapping("useredit/{user}")
    public String userEdit(Model model,
                           @PathVariable User user){
        model.addAttribute("title", "useredit");
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "user-edit";
    }

    @PostMapping("useredit/{user}")
    public String userSave(
                            @PathVariable User user,
                            @RequestParam String username,
                            @RequestParam String password,
                            @RequestParam Map<String, String> form,
                            @RequestParam("userId") User userT
    ){
        userService.saveUser(userT, username, password, form);
        return "redirect:/admin/userlist";
    }

    @PostMapping("/useredit/{user}/userdelete")
    public String userDelete(
            @PathVariable User user,
            @RequestParam("userId") User userT
    ){
        userService.deleteUser(userT);
        return "redirect:/admin/userlist";
    }
}
