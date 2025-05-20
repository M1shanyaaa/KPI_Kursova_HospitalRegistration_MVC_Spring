package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.HistoryPatientsModel;
import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.HistoryPatientRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Контролер для перегляду історії пацієнтів.
 * Доступ дозволено лише автентифікованим користувачам.
 */
@Controller
public class HistoryPatientsController {

    private final HistoryPatientRepo historyPatientRepo;

    /**
     * Конструктор контролера історії пацієнтів.
     *
     * @param historyPatientRepo репозиторій історії пацієнтів
     */
    public HistoryPatientsController(HistoryPatientRepo historyPatientRepo) {
        this.historyPatientRepo = historyPatientRepo;
    }

    /**
     * Обробляє запит на перегляд історії пацієнтів.
     * Додає до моделі список історій пацієнтів для відображення у view.
     *
     * @param model   модель для передачі даних у представлення
     * @param session HTTP-сесія для отримання автентифікованого користувача
     * @return сторінка "history-patients" або редірект на головну сторінку
     */
    @GetMapping("/historypatients")
    public String getHistory(Model model, HttpSession session) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }

        List<HistoryPatientsModel> patients = historyPatientRepo.findAll();
        model.addAttribute("user", user);
        model.addAttribute("patients", patients);
        return "history-patients";
    }
}
