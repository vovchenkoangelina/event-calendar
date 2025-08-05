package com.boardclub.eventcalendar.controller;

import com.boardclub.eventcalendar.model.Event;
import com.boardclub.eventcalendar.model.EventRegistration;
import com.boardclub.eventcalendar.model.User;
import com.boardclub.eventcalendar.service.EventService;
import com.boardclub.eventcalendar.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        List<EventRegistration> registrations = eventService.findRegistrationsByUser(user);
        model.addAttribute("registrations", registrations);
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
}
