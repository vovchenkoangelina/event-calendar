package com.boardclub.eventcalendar;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EventCalendarApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventCalendarApplication.class, args);
	}

	@Bean
	public CommandLineRunner printPasswordHash(PasswordEncoder passwordEncoder) {
		return args -> {
			System.out.println("Хеш пароля admin123: " + passwordEncoder.encode("admin123"));
		};
	}
}