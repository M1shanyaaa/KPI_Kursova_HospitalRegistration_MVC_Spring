package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.HistoryPatientsModel;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.HistoryPatientRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HistoryPatientsController {
    private final HistoryPatientRepo historyPatientRepo;

    public HistoryPatientsController(HistoryPatientRepo historyPatientRepo) {
        this.historyPatientRepo = historyPatientRepo;
    }

    @GetMapping("/historypatients")
    public String getHistory(Model model, HttpSession session,
                             @RequestParam(value = "search", required = false) String searchTerm,
                             @RequestParam(value = "searchType", required = false, defaultValue = "all") String searchType) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/";
        }
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        // Визначаємо чи є пошуковий запит
        boolean hasSearchTerm = searchTerm != null && !searchTerm.trim().isEmpty();
        String cleanSearchTerm = hasSearchTerm ? searchTerm.trim() : "";

        List<HistoryPatientsModel> patients = getFilteredPatientsForAllUsers(cleanSearchTerm, searchType);
        model.addAttribute("user", user);
        model.addAttribute("patients", patients);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("searchType", searchType);
        model.addAttribute("totalPatients", patients.size());
        return "history-patients";
    }

    private List<HistoryPatientsModel> getFilteredPatientsForAllUsers(String searchTerm, String searchType) {
        switch (searchType) {
            case "name":
                return historyPatientRepo.findByFullNameContainingIgnoreCase(searchTerm);
            case "phone":
                return historyPatientRepo.findByPhoneContaining(searchTerm);
            case "diagnosis":
                return historyPatientRepo.findByDiagnosisContainingIgnoreCase(searchTerm);
            case "dischargeDATE":
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate date = LocalDate.parse(searchTerm, formatter);
                    LocalDateTime from = date.atStartOfDay();
                    LocalDateTime to = date.atTime(LocalTime.MAX);
                    return historyPatientRepo.findByDischargeDate(from, to);
                } catch (DateTimeParseException e) {
                    // Якщо введено не дату — повернути порожній список або всі записи, або логувати помилку
                    return new ArrayList<>();
                }
            case "recordedDATE":
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate date = LocalDate.parse(searchTerm, formatter);
                    LocalDateTime from = date.atStartOfDay();
                    LocalDateTime to = date.atTime(LocalTime.MAX);
                    return historyPatientRepo.findByRecordedDate(from, to);
                } catch (DateTimeParseException e) {
                    // Якщо введено не дату — повернути порожній список або всі записи, або логувати помилку
                    return new ArrayList<>();
                }
            case "all":
            default:
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate date = LocalDate.parse(searchTerm, formatter);
                    LocalDateTime from = date.atStartOfDay();
                    LocalDateTime to = date.atTime(LocalTime.MAX);
                    return historyPatientRepo.findByDateFields(from, to);
                } catch (DateTimeParseException e) {
                    return historyPatientRepo.findByAllFieldsContaining(searchTerm);
                }
        }
    }
}

