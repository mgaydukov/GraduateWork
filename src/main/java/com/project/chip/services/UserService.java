package com.project.chip.services;

import com.project.chip.models.Role;
import com.project.chip.models.User;
import com.project.chip.repos.UserRepo;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public boolean addUser(User user){
        User userFromDb = userRepo.findByUsername(user.getUsername());

        if (userFromDb != null){
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setDateRegistration(String.valueOf(LocalDate.now()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setBio("No bio yet.");
        user.setProfilePic("https://i.imgur.com/DTgSoTS.png");
        userRepo.save(user);
        return true;
    }

    public void saveUser(User user, @NotNull String username, String password, Map<String, String> form) {
        if (!username.isEmpty())
            user.setUsername(username);

        if (!password.isEmpty())
            user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()){
            if (roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepo.save(user);
    }

    public void updateProfile(User user, String username, String bio, String password) {
        if (!username.isEmpty())
            user.setUsername(username);

        if (!password.isEmpty())
            user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setBio(bio);
        userRepo.save(user);
    }

    public void updateProfile(User user, String username, String bio, String profilePic, String password) {
        if (!username.isEmpty())
            user.setUsername(username);

        if (!password.isEmpty())
            user.setPassword(passwordEncoder.encode(password));

        if (!profilePic.isEmpty())
            user.setProfilePic(profilePic);

        user.setBio(bio);
        userRepo.save(user);
    }

    public void deleteUser(User user) {
        userRepo.delete(user);
    }
}
