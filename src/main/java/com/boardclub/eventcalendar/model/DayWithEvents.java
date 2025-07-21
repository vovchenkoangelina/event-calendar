package com.boardclub.eventcalendar.model;

import java.time.LocalDate;
import java.util.List;

public class DayWithEvents {
    private LocalDate date;
    private List<Event> events;

    public DayWithEvents(LocalDate date, List<Event> events) {
        this.date = date;
        this.events = events;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Event> getEvents() {
        return events;
    }
}

