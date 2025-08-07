package com.boardclub.eventcalendar.model;

import java.time.LocalDate;
import java.util.List;

public class DayWithEvents {
    private LocalDate date;
    private List<Event> events;
    private boolean currentMonth;

    public DayWithEvents(LocalDate date, List<Event> events, boolean currentMonth) {
        this.date = date;
        this.events = events;
        this.currentMonth = currentMonth;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Event> getEvents() {
        return events;
    }

    public boolean isCurrentMonth() {
        return currentMonth;
    }
}