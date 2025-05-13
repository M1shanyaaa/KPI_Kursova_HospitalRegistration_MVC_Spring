package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PatientModel;
import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.PatientRepo;
import hospital_registration.demo.repo.PersonalRepo;
import hospital_registration.demo.service.AuthorizationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients")
public class AddPatientController {

    private final PatientRepo patientRepo;
    private final PersonalRepo doctorRepo;

    @Autowired
    private AuthorizationService authService;

    @Autowired
    public AddPatientController(PatientRepo patientRepo, PersonalRepo doctorRepo) {
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
    }

    @GetMapping("/add")
    public String showAddPatientForm(Model model, HttpSession session) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (!authService.hasNurseAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }
        model.addAttribute("patient", new PatientModel());
        model.addAttribute("doctors", doctorRepo.findAll());
        return "patient-record";
    }

    @PostMapping("/add")
    public String addPatient(
            @Valid @ModelAttribute("patient") PatientModel patient,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (!authService.hasNurseAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("doctors", doctorRepo.findAll());
            return "patient-record";
        }

        // Отримання id лікаря, який передається у формі
        Long doctorId = patient.getDoctor() != null ? patient.getDoctor().getId() : null;

        if (doctorId == null || !doctorRepo.existsById(doctorId)) {
            bindingResult.rejectValue("doctor", "error.patient", "Лікар не обраний або не існує.");
            model.addAttribute("doctors", doctorRepo.findAll());
            return "patient-record";
        }

        // Завантаження лікаря з бази
        PersonalModel doctor = doctorRepo.findById(doctorId).orElse(null);
        patient.setDoctor(doctor); // Встановлюємо повноцінний об'єкт

        // Зберігаємо пацієнта
        patientRepo.save(patient);

        redirectAttributes.addFlashAttribute("successMessage", "Пацієнта успішно додано!");
        return "redirect:/patients/add";
    }
}
