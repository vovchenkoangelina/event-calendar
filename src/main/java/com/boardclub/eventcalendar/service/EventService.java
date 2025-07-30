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

    public void updateEvent(Long id, Event updatedEvent) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setTitle(updatedEvent.getTitle());
        event.setStartTime(updatedEvent.getStartTime());
        event.setPrice(updatedEvent.getPrice());
        event.setComplicacy(updatedEvent.getComplicacy());
        event.setHost(updatedEvent.getHost());
        event.setDescription(updatedEvent.getDescription());
        event.setMaxParticipants(updatedEvent.getMaxParticipants());
        event.setTables(updatedEvent.getTables());

        eventRepository.save(event);
    }

    public void deleteById(Long id) {
        eventRepository.deleteById(id);
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

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Событие не найдено"));
    }

    public void removeUserFromEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Событие не найдено"));
        User userToRemove = event.getRegisteredUsers().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Пользователь не найден в списке записавшихся"));

        event.getRegisteredUsers().remove(userToRemove);
        eventRepository.save(event);
    }
}
