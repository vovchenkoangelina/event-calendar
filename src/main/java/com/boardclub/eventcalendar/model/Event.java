package com.boardclub.eventcalendar.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    private String title;

    private String price;

    private String complicacy;

    private String host;

    private String description;

    private LocalDateTime startTime;

    private Integer maxParticipants;

    private Integer tables;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<EventRegistration> registrations = new HashSet<>();

    public int getTotalParticipantsCount() {
        return registrations.stream()
                .filter(reg -> !Boolean.TRUE.equals(reg.isReserve())) // исключаем резерв
                .mapToInt(reg -> 1 + reg.getAdditionalGuests())
                .sum();
    }


    public Set<EventRegistration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(Set<EventRegistration> registrations) {
        this.registrations = registrations;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getComplicacy() {
        return complicacy;
    }

    public void setComplicacy(String complicacy) {
        this.complicacy = complicacy;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getTables() {
        return tables;
    }

    public void setTables(int tables) {
        this.tables = tables;
    }
}