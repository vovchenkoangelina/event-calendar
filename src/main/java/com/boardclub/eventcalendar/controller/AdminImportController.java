package com.boardclub.eventcalendar.controller;

import com.boardclub.eventcalendar.service.CsvEventImporter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//для импорта игротек с csv гугл-таблички

@Controller
@RequiredArgsConstructor
public class AdminImportController {

    private final CsvEventImporter importer;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/import-csv")
    public String importCsvData() {
        importer.importFromCsv("src/main/resources/events.csv");
        return "redirect:/home";
    }
}

