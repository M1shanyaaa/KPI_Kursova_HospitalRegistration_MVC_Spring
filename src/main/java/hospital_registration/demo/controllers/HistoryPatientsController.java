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

/**
 * Контролер для обробки запитів, пов'язаних з історією пацієнтів.
 * Надає можливість переглядати історію пацієнтів та здійснювати пошук за різними параметрами.
 */
@Controller
public class HistoryPatientsController {

    /** Репозиторій для доступу до даних історії пацієнтів */
    private final HistoryPatientRepo historyPatientRepo;

    /**
     * Конструктор контролера HistoryPatientsController.
     *
     * @param historyPatientRepo репозиторій історії пацієнтів
     */
    public HistoryPatientsController(HistoryPatientRepo historyPatientRepo) {
        this.historyPatientRepo = historyPatientRepo;
    }

    /**
     * Обробляє GET-запити на сторінку історії пацієнтів.
     * Дозволяє переглядати всі записи або фільтрувати їх за заданими критеріями.
     *
     * @param model модель для передачі даних у шаблон
     * @param session поточна сесія користувача
     * @param searchTerm рядок пошуку (необов'язковий)
     * @param searchType тип пошуку (наприклад: name, phone, diagnosis, dischargeDATE, recordedDATE, all)
     * @return назва шаблону для рендерингу (history-patients або редірект на головну сторінку)
     */
    @GetMapping("/historypatients")
    public String getHistory(Model model, HttpSession session,
                             @RequestParam(value = "search", required = false) String searchTerm,
                             @RequestParam(value = "searchType", required = false, defaultValue = "all") String searchType) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/";
        }

        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
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

    /**
     * Повертає список пацієнтів, відфільтрованих за заданим типом пошуку та пошуковим запитом.
     *
     * @param searchTerm текст пошуку
     * @param searchType тип пошуку (наприклад: name, phone, diagnosis, dischargeDATE, recordedDATE, all)
     * @return список пацієнтів, що відповідають критеріям пошуку
     */
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
                    // Якщо введено не дату — повернути порожній список
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
