package com.boardclub.eventcalendar.service;


import com.boardclub.eventcalendar.model.Event;
import com.boardclub.eventcalendar.model.User;
import com.boardclub.eventcalendar.repository.EventRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEventsForDay(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return eventRepository.findByStartTimeBetween(startOfDay, endOfDay);
    }

    public List<Event> searchEvents(String keyword) {
        return eventRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public void save(Event event) {
        eventRepository.save(event);
    }

    public Event registerUserToEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Событие не найдено"));

        if (event.getRegisteredUsers().size() >= event.getMaxParticipants()) {
            throw new RuntimeException("Достигнуто максимальное число участников");
        }

        event.getRegisteredUsers().add(user);
        eventRepository.save(event);
        return event;  // вернуть событие для контроллера
    }

}
