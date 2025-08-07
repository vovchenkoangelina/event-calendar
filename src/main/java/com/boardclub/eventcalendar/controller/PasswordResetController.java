package com.boardclub.eventcalendar.controller;

import com.boardclub.eventcalendar.model.PasswordResetToken;
import com.boardclub.eventcalendar.model.User;
import com.boardclub.eventcalendar.repository.PasswordResetTokenRepository;
import com.boardclub.eventcalendar.service.EmailService;
import com.boardclub.eventcalendar.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ✅ правильный импорт
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PasswordResetController {

    private final UserService userService;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetController(UserService userService,
                                   PasswordResetTokenRepository tokenRepository,
                                   EmailService emailService,
                                   PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<User> userOpt = Optional.ofNullable(userService.findByEmail(email));

        if (userOpt.isPresent()) {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(userOpt.get());
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
            tokenRepository.save(resetToken);

            String resetLink = "http://localhost:8080/reset-password?token=" + token;
            emailService.send(email, "Восстановление пароля", "Ссылка для сброса пароля: " + resetLink);
        }

        model.addAttribute("message", "Если такой email существует, инструкция отправлена.");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isPresent() && tokenOpt.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            model.addAttribute("token", token);
            return "reset-password";
        }

        model.addAttribute("message", "Ссылка недействительна или устарела.");
        return "reset-password-error";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token,
                                      @RequestParam("password") String password,
                                      Model model) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isPresent() && tokenOpt.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            User user = tokenOpt.get().getUser();
            user.setPassword(passwordEncoder.encode(password));
            userService.save(user);
            tokenRepository.delete(tokenOpt.get());

            model.addAttribute("message", "Пароль успешно сброшен.");
            return "login"; // можно заменить на redirect:/login
        }

        model.addAttribute("message", "Ссылка недействительна или устарела.");
        return "reset-password-error";
    }
}
