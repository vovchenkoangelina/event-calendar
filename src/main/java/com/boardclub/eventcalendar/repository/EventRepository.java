package com.boardclub.eventcalendar.repository;

import com.boardclub.eventcalendar.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStartTime(LocalDateTime start);

    List<Event> findByTitleContainingIgnoreCase(String keyword);
}