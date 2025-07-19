package com.boardclub.eventcalendar.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private EventType type; // Игротека или Турнир

    private String description;

    private LocalDateTime startTime;

    private int maxParticipants;

    // геттеры и сеттеры
}