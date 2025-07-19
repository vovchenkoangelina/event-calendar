package com.boardclub.eventcalendar.repository;

import com.boardclub.eventcalendar.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}