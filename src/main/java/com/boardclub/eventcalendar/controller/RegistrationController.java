package com.boardclub.eventcalendar.controller;

import com.boardclub.eventcalendar.dto.UserDto;
import com.boardclub.eventcalendar.model.User;
import com.boardclub.eventcalendar.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userDto") UserDto userDto, Model model) {

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Пароли не совпадают");
            return "register";
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            model.addAttribute("errorMessage", "Пользователь с таким email уже существует");
            return "register";
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        long userCount = userRepository.count();
        if (userCount < 2) {
            user.getRoles().add("ROLE_ADMIN");
        } else {
            user.getRoles().add("ROLE_USER");
        }

        userRepository.save(user);

        return "redirect:/login?registered";
    }
}