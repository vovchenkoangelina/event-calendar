package com.boardclub.eventcalendar.service;

import com.boardclub.eventcalendar.model.Event;
import com.boardclub.eventcalendar.model.EventRegistration;
import com.boardclub.eventcalendar.model.User;
import com.boardclub.eventcalendar.repository.EventRegistrationRepository;
import com.boardclub.eventcalendar.repository.EventRepository;
import com.boardclub.eventcalendar.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

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
                .orElseThrow(() -> new RuntimeException("Событие не найдено"));

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

    public void registerUserToEvent(Long eventId, User user, Integer additionalGuests, String comment) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Событие не найдено"));

        int currentCount = event.getTotalParticipantsCount();

        if ((currentCount + 1 + additionalGuests) > event.getMaxParticipants()) {
            throw new RuntimeException("Достигнуто максимальное число участников с учётом дополнительных гостей");
        }

        boolean alreadyRegistered = eventRegistrationRepository.existsByEventAndUser(event, user);
        if (alreadyRegistered) {
            throw new RuntimeException("Вы уже записаны на это мероприятие");
        }

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);
        registration.setAdditionalGuests(additionalGuests);
        registration.setComment(comment);
        registration.setReserve(false); // Основной список

        eventRegistrationRepository.save(registration);
    }

    public void registerUserToEventReserve(Long eventId, User user, Integer additionalGuests, String comment) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Событие не найдено"));

        boolean alreadyRegistered = eventRegistrationRepository.existsByEventAndUser(event, user);
        if (alreadyRegistered) {
            throw new RuntimeException("Вы уже зарегистрированы (в списке или в резерве)");
        }

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);
        registration.setAdditionalGuests(additionalGuests);
        registration.setComment(comment);
        registration.setReserve(true);

        System.out.println(">>> Регистрируем в резерв: " + user.getUsername() + ", guests: " + additionalGuests);
        System.out.println(">>> reserve = " + registration.isReserve());

        eventRegistrationRepository.save(registration);
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Событие не найдено"));
    }

    @Transactional
    public void removeRegistration(EventRegistration registration) {
        Event event = registration.getEvent();
        if (event != null) {
            event.getRegistrations().remove(registration);
        }
        eventRegistrationRepository.delete(registration);
    }

    public EventRegistration findRegistrationByEventAndUser(Long eventId, Long userId) {
        return eventRegistrationRepository.findByEventIdAndUserId(eventId, userId);
    }

    public void removeUserFromReserve(Long eventId, Long userId) {
        Event event = eventRepository.findByIdWithRegistrations(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        EventRegistration registration = eventRegistrationRepository.findByEventIdAndUserId(eventId, userId);
        if (registration != null) {
            event.getRegistrations().remove(registration);
            eventRegistrationRepository.delete(registration);
        }
    }

    public List<EventRegistration> findRegistrationsByUser(User user) {
        return eventRegistrationRepository.findByUserId(user.getId());
    }

    public EventRegistration findRegistrationById(Long id) {
        return eventRegistrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Регистрация не найдена"));
    }

    public void saveRegistration(EventRegistration registration) {
        eventRegistrationRepository.save(registration);
    }

}