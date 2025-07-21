package com.boardclub.eventcalendar.controller;

import com.boardclub.eventcalendar.model.Event;
import com.boardclub.eventcalendar.service.EventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showNewEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "event-form";
    }

    @PostMapping("/events/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveEvent(@ModelAttribute("event") Event event) {
        eventService.save(event);
        return "redirect:/home";
    }
}
