package com.boardclub.eventcalendar.repository;

import com.boardclub.eventcalendar.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findByTitleContainingIgnoreCase(String keyword);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.registrations WHERE e.id = :id")
    Optional<Event> findByIdWithRegistrations(@Param("id") Long id);
}