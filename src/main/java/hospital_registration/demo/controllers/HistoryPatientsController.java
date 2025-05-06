package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.HistoryPatientsModel;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.HistoryPatientRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HistoryPatientsController {
    private final HistoryPatientRepo historyPatientRepo;

    public HistoryPatientsController(HistoryPatientRepo historyPatientRepo) {
        this.historyPatientRepo = historyPatientRepo;
    }

    @GetMapping("/historypatients")
    public String getHistory(Model model, HttpSession session) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/";
        }
        List<HistoryPatientsModel> patients = historyPatientRepo.findAll();
        model.addAttribute("user", user);
        model.addAttribute("patients", patients);
        return "history-patients";
    }
}

