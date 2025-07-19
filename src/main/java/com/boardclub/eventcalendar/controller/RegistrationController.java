package com.boardclub.eventcalendar.controller;

import com.boardclub.eventcalendar.model.User;
import com.boardclub.eventcalendar.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Thymeleaf шаблон register.html
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, BindingResult result, Model model) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            result.rejectValue("email", "error.user", "Пользователь с таким email уже существует");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add("ROLE_USER");
        userRepository.save(user);
        return "redirect:/login"; // после регистрации — страница логина
    }
}
