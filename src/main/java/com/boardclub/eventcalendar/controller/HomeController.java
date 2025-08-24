package com.boardclub.eventcalendar.controller;

import com.boardclub.eventcalendar.model.DayWithEvents;
import com.boardclub.eventcalendar.service.EventService;
import com.boardclub.eventcalendar.model.Event;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class HomeController {

    private final EventService eventService;

    public HomeController(EventService eventService) {
        this.eventService = eventService;
    }

    private static final String[] MONTHS_NOMINATIVE = {
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    };

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String showCalendar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer prev,
            @RequestParam(required = false) Integer next,
            @RequestParam(defaultValue = "month") String view,
            Model model
    ) {
        LocalDate baseDate = (date != null) ? date : LocalDate.now();
        LocalDate currentDate;

        if ("week".equals(view)) {
            if (prev != null) {
                currentDate = baseDate.minusWeeks(1);
            } else if (next != null) {
                currentDate = baseDate.plusWeeks(1);
            } else {
                currentDate = baseDate;
            }

            LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);

            List<DayWithEvents> weekDays = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate dayDate = startOfWeek.plusDays(i);
                List<Event> events = eventService.getEventsForDay(dayDate);
                weekDays.add(new DayWithEvents(dayDate, events, true));
            }

            model.addAttribute("weekDays", weekDays);
            model.addAttribute("currentDate", currentDate);
            model.addAttribute("startOfWeek", startOfWeek);
            model.addAttribute("view", "week");

        } else {
            if (prev != null) {
                currentDate = baseDate.minusMonths(1);
            } else if (next != null) {
                currentDate = baseDate.plusMonths(1);
            } else {
                currentDate = baseDate;
            }

            YearMonth yearMonth = YearMonth.from(currentDate);
            String monthRu = MONTHS_NOMINATIVE[currentDate.getMonthValue() - 1];
            model.addAttribute("monthRu", monthRu);
            model.addAttribute("year", currentDate.getYear());

            LocalDate firstOfMonth = yearMonth.atDay(1);
            DayOfWeek firstWeekDay = firstOfMonth.getDayOfWeek();
            int shift = firstWeekDay.getValue() - 1;

            LocalDate calendarStart = firstOfMonth.minusDays(shift);
            List<List<DayWithEvents>> weeks = new ArrayList<>();

            for (int week = 0; week < 6; week++) {
                List<DayWithEvents> weekDays = new ArrayList<>();
                for (int day = 0; day < 7; day++) {
                    LocalDate dayDate = calendarStart.plusDays(week * 7 + day);
                    List<Event> events = eventService.getEventsForDay(dayDate);
                    boolean isCurrentMonth = dayDate.getMonth() == currentDate.getMonth();
                    weekDays.add(new DayWithEvents(dayDate, events, isCurrentMonth));
                }
                weeks.add(weekDays);
            }

            model.addAttribute("weeks", weeks);
            model.addAttribute("currentDate", currentDate);
            model.addAttribute("view", "month");
        }

        return "home";
    }

}
