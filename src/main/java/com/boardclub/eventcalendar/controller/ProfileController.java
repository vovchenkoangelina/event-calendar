package com.boardclub.eventcalendar.controller;

import com.boardclub.eventcalendar.model.Event;
import com.boardclub.eventcalendar.model.EventRegistration;
import com.boardclub.eventcalendar.model.User;
import com.boardclub.eventcalendar.service.EventService;
import com.boardclub.eventcalendar.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/profile")
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private final EventService eventService;
    private final UserService userService;

    public ProfileController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }


    @GetMapping
    public String showProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<EventRegistration> allRegistrations = eventService.findRegistrationsByUser(user);

        List<EventRegistration> mainRegistrations = allRegistrations.stream()
                .filter(reg -> !reg.isReserve())
                .toList();

        List<EventRegistration> reserveRegistrations = allRegistrations.stream()
                .filter(EventRegistration::isReserve)
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("mainRegistrations", mainRegistrations);
        model.addAttribute("reserveRegistrations", reserveRegistrations);

        return "user-profile";
    }


    @PostMapping("/delete/{id}")
    public String deleteRegistration(@PathVariable Long id, Principal principal) {
        EventRegistration registration = eventService.findRegistrationById(id);
        if (registration.getUser().getEmail().equals(principal.getName())) {
            eventService.deleteRegistration(id);
        }
        return "redirect:/profile";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        EventRegistration registration = eventService.findRegistrationById(id);
        if (!registration.getUser().getEmail().equals(principal.getName())) {
            return "redirect:/profile";
        }
        model.addAttribute("registration", registration);
        return "edit-registration";
    }

    @PostMapping("/edit/{id}")
    public String updateRegistration(@PathVariable Long id,
                                     @RequestParam String comment,
                                     @RequestParam int additionalGuests,
                                     Principal principal) {
        EventRegistration registration = eventService.findRegistrationById(id);
        int oldAdditionalGuests = registration.getAdditionalGuests();
        if (registration.getUser().getEmail().equals(principal.getName())) {
            registration.setComment(comment);
            registration.setAdditionalGuests(additionalGuests);
            eventService.saveRegistration(registration);
        }
        return "redirect:/profile";
    }

    @GetMapping("/edit")
    public String showEditProfileForm(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute("user") User updatedUser,
                                BindingResult bindingResult,
                                @RequestParam("newPassword") String newPassword,
                                Model model,
                                Principal principal) {
        User existingUser = userService.findByEmail(principal.getName());

        // Проверка: email уже занят другим
        if (!updatedUser.getEmail().equals(existingUser.getEmail()) &&
                userService.emailExists(updatedUser.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email уже занят");
        }

        if (bindingResult.hasErrors()) {
            return "edit-profile";
        }

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setTelegram(updatedUser.getTelegram());

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            userService.updatePassword(existingUser, newPassword);
        }

        userService.save(existingUser);

        return "redirect:/profile";
    }

}
