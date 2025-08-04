package com.boardclub.eventcalendar.repository;

import com.boardclub.eventcalendar.model.Event;
import com.boardclub.eventcalendar.model.EventRegistration;
import com.boardclub.eventcalendar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByEventAndUser(Event event, User user);
    List<EventRegistration> findByUser(User user);
}