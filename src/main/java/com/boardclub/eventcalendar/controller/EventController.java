package com.boardclub.eventcalendar.controller;

import com.boardclub.eventcalendar.model.Event;
import com.boardclub.eventcalendar.model.User;
import com.boardclub.eventcalendar.repository.UserRepository;
import com.boardclub.eventcalendar.service.EventService;
import com.boardclub.eventcalendar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@Controller
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    @Autowired
    private UserRepository userRepository;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("/events/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showNewEventForm(@RequestParam(required = false) String date, Model model) {
        Event event = new Event();
        String displayDate = "";

        if (date != null) {
            try {
                LocalDate localDate = LocalDate.parse(date);
                event.setStartTime(localDate.atTime(12, 0));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM (EEEE)", new Locale("ru"));
                displayDate = localDate.format(formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка разбора даты: " + date);
            }
        }

        model.addAttribute("event", event);
        model.addAttribute("startTimeStr", event.getStartTime() != null
                ? event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                : "");
        System.out.println("startTime: " + event.getStartTime());
        model.addAttribute("displayDate", displayDate);
        return "event-form";
    }

//    @PostMapping("/events/new")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String saveEvent(@RequestParam String startTimeStr, @ModelAttribute Event event) {
//        event.setStartTime(LocalDateTime.parse(startTimeStr));
//        eventService.save(event);
//        return "redirect:/home";
//    }

    @PostMapping("/events/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveEvent(@RequestParam String startTimeStr, @RequestParam String date, @ModelAttribute Event event) {
        LocalDate localDate = LocalDate.parse(date);
        LocalTime localTime = LocalTime.parse(startTimeStr);
        event.setStartTime(LocalDateTime.of(localDate, localTime));
        eventService.save(event);
        return "redirect:/home";
    }

    @GetMapping("/events/day")
    public String getEventsForDay(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {
        List<Event> events = eventService.getEventsForDay(date);
        model.addAttribute("date", date);
        model.addAttribute("events", events);
        return "day-events";
    }

    @PostMapping("/events/{id}/register")
    public String registerToEvent(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());

        try {
            Event event = eventService.registerUserToEvent(id, user);
            redirectAttributes.addFlashAttribute("message", "Вы записаны на " + event.getTitle());
        } catch (RuntimeException e) {
            // Например, если превышен максимум участников
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/home";
    }
}
