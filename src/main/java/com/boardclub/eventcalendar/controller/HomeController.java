package com.boardclub.eventcalendar.controller;

import com.boardclub.eventcalendar.model.DayWithEvents;
import com.boardclub.eventcalendar.service.EventService;
import com.boardclub.eventcalendar.model.Event;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private final EventService eventService;

    public HomeController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String showCalendar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "month") String view,
            Model model
    ) {
        LocalDate currentDate = (date != null) ? date : LocalDate.now();
        YearMonth yearMonth = YearMonth.from(currentDate);

        LocalDate firstOfMonth = yearMonth.atDay(1);
        DayOfWeek firstWeekDay = firstOfMonth.getDayOfWeek();
        int shift = firstWeekDay.getValue() - 1; // сделать понедельник = 0, воскресенье = 6

        LocalDate calendarStart = firstOfMonth.minusDays(shift);
        List<List<DayWithEvents>> weeks = new ArrayList<>();

        for (int week = 0; week < 6; week++) {
            List<DayWithEvents> weekDays = new ArrayList<>();
            for (int day = 0; day < 7; day++) {
                LocalDate dayDate = calendarStart.plusDays(week * 7 + day);
                List<Event> events = eventService.getEventsForDay(dayDate);
                weekDays.add(new DayWithEvents(dayDate, events));
            }
            weeks.add(weekDays);
        }

        model.addAttribute("weeks", weeks);
        model.addAttribute("currentDate", currentDate);
        return "home";
    }
}
