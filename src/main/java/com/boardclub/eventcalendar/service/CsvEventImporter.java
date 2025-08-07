package com.boardclub.eventcalendar.service;

import com.boardclub.eventcalendar.model.Event;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
public class CsvEventImporter {

    @Autowired
    private EventService eventService;

    private static final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("d.MM.yyyy", Locale.forLanguageTag("ru"));
    private static final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH:mm");

    private static final Pattern datePattern = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}");

    public void importFromCsv(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String[] line;
            boolean isFirst = true;
            int rowNumber = 1;

            while ((line = reader.readNext()) != null) {
                rowNumber++;

                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                try {
                    // Читаем значения по колонкам
                    String dateStr         = safeGet(line, 0); // Дата
                    String title           = safeGet(line, 1); // Название
                    String price           = safeGet(line, 2); // Стоимость
                    String participantsStr = safeGet(line, 3); // Кол-во человек
                    String timeStr         = safeGet(line, 4); // Время
                    String complicacy      = safeGet(line, 5); // Сложность
                    String description     = safeGet(line, 6); // Описание
                    String host            = safeGet(line, 7); // Организатор
                    String tablesStr       = safeGet(line, 8); // Количество столов

                    // Обработка даты
                    String cleanedDateStr = extractDate(dateStr);
                    if (cleanedDateStr.isEmpty()) {
                        System.out.println("⛔ Не удалось распознать дату на строке " + rowNumber + ": " + dateStr);
                        continue;
                    }

                    LocalDate date = LocalDate.parse(cleanedDateStr, dateFormatter);
                    LocalTime time = parseTime(timeStr);
                    if (time == null) {
                        System.out.println("⛔ Не удалось распарсить время на строке " + rowNumber + ": " + timeStr);
                        continue;
                    }
                    LocalDateTime startTime = LocalDateTime.of(date, time);

                    // Создаём Event
                    Event event = new Event();
                    event.setTitle(title);
                    event.setPrice(price);
                    event.setComplicacy(complicacy);
                    event.setDescription(description);
                    event.setHost(host);
                    event.setStartTime(startTime);
                    event.setMaxParticipants(parseInteger(participantsStr));
                    event.setTables(parseInteger(tablesStr));

                    eventService.save(event);
                    System.out.println("✅ Сохранено: " + title + " (" + startTime + ")");

                } catch (Exception e) {
                    System.out.println("⚠️ Ошибка на строке " + rowNumber + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Не удалось открыть CSV-файл: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Integer parseInteger(String value) {
        try {
            if (value == null || value.isBlank()) return null;
            String cleaned = value.replaceAll("[^\\d]", "");
            return cleaned.isEmpty() ? null : Integer.parseInt(cleaned);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractDate(String raw) {
        if (raw == null) return "";
        Matcher matcher = datePattern.matcher(raw);
        return matcher.find() ? matcher.group() : "";
    }

    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) return null;

        // Подходит под 13:00, 13.00, 13-00
        Pattern pattern = Pattern.compile("(\\d{1,2})[:.-](\\d{2})");
        Matcher matcher = pattern.matcher(timeStr);
        if (matcher.find()) {
            String hour = matcher.group(1);
            String minute = matcher.group(2);
            return LocalTime.parse(hour + ":" + minute, timeFormatter);
        }

        return null;
    }

    private String safeGet(String[] arr, int index) {
        return (index < arr.length && arr[index] != null) ? arr[index].trim() : "";
    }
}
