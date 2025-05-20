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

/**
 * Контролер для додавання пацієнтів до системи.
 * Доступ надається лише медичним сестрам.
 */
@Controller
@RequestMapping("/patients")
public class AddPatientController {

    private final PatientRepo patientRepo;
    private final PersonalRepo doctorRepo;

    @Autowired
    private AuthorizationService authService;

    /**
     * Конструктор із залежностями репозиторіїв пацієнтів та лікарів.
     *
     * @param patientRepo репозиторій пацієнтів
     * @param doctorRepo  репозиторій лікарів
     */
    @Autowired
    public AddPatientController(PatientRepo patientRepo, PersonalRepo doctorRepo) {
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
    }

    /**
     * Відображає форму для додавання нового пацієнта.
     * Доступно лише для користувачів із правами медсестри.
     *
     * @param model   модель для передачі даних у представлення
     * @param session HTTP-сесія для перевірки авторизації
     * @return сторінка з формою або редірект при відсутності доступу
     */
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

    /**
     * Обробляє відправку форми додавання пацієнта.
     * Виконує валідацію, перевіряє вибраного лікаря, зберігає пацієнта в базу.
     *
     * @param patient             пацієнт, заповнений з форми
     * @param bindingResult       об'єкт із результатами валідації
     * @param redirectAttributes  атрибути для передачі повідомлення після редіректу
     * @param session             HTTP-сесія для перевірки доступу
     * @param model               модель для повторного показу форми при помилках
     * @return сторінка з формою або редірект на успішне додавання
     */
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

        Long doctorId = patient.getDoctor() != null ? patient.getDoctor().getId() : null;

        if (doctorId == null || !doctorRepo.existsById(doctorId)) {
            bindingResult.rejectValue("doctor", "error.patient", "Лікар не обраний або не існує.");
            model.addAttribute("doctors", doctorRepo.findAll());
            return "patient-record";
        }

        PersonalModel doctor = doctorRepo.findById(doctorId).orElse(null);
        patient.setDoctor(doctor);

        patientRepo.save(patient);

        redirectAttributes.addFlashAttribute("successMessage", "Пацієнта успішно додано!");
        return "redirect:/patients/add";
    }
}
