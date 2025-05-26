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
 * Контролер для редагування даних пацієнтів.
 * Доступ надається лікарям та головному лікарю.
 */
@Controller
@RequestMapping("/patients")
public class EditPatientController {

    private final PatientRepo patientRepo;
    private final PersonalRepo personalRepo;

    @Autowired
    private AuthorizationService authService;

    /**
     * Конструктор із залежностями репозиторіїв.
     *
     * @param patientRepo репозиторій пацієнтів
     * @param personalRepo репозиторій персоналу
     */
    @Autowired
    public EditPatientController(PatientRepo patientRepo, PersonalRepo personalRepo) {
        this.patientRepo = patientRepo;
        this.personalRepo = personalRepo;
    }

    /**
     * Відображає форму для редагування даних пацієнта.
     * Доступно лікарям для їх пацієнтів і головному лікарю для всіх пацієнтів.
     *
     * @param patientId ID пацієнта для редагування
     * @param model модель для передачі даних у представлення
     * @param session HTTP-сесія для перевірки авторизації
     * @return сторінка з формою редагування або редірект при відсутності доступу
     */
    @GetMapping("/edit/{patientId}")
    public String showEditPatientForm(@PathVariable Long patientId, Model model, HttpSession session) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");

        // Перевірка автентифікації
        if (loggedInUser == null) {
            return "redirect:/";
        }

        // Перевірка прав доступу
        if (!authService.hasDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        // Отримання пацієнта
        PatientModel patient = patientRepo.findById(patientId).orElse(null);
        if (patient == null) {
            return "redirect:/error";
        }
        if (patient.getDoctor() == null) {
            patient.setDoctor(new PersonalModel()); // Заповнити порожнього лікаря, щоб уникнути NPE
        }

        // Додаткова перевірка: лікар може редагувати тільки своїх пацієнтів
        // Головний лікар може редагувати всіх пацієнтів
        if (authService.isDoctor(loggedInUser) && !loggedInUser.getId().equals(patient.getDoctor().getId())) {
            return "redirect:/access-denied";
        }
        System.out.println(patient.getBirthDate());

        model.addAttribute("patient", patient);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("doctors", personalRepo.findAll());

        return "edit-patient";
    }

    /**
     * Обробляє відправку форми редагування пацієнта.
     * Виконує валідацію та оновлює дані пацієнта в базі.
     *
     * @param patientId ID пацієнта
     * @param patient оновлені дані пацієнта
     * @param bindingResult результати валідації
     * @param redirectAttributes атрибути для передачі повідомлення
     * @param session HTTP-сесія для перевірки доступу
     * @param model модель для повторного показу форми при помилках
     * @return редірект на успішне оновлення або повторний показ форми
     */
    @PostMapping("/edit/{patientId}")
    public String updatePatient(
            @PathVariable Long patientId,
            @Valid @ModelAttribute("patient") PatientModel patient,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");

        // Перевірка автентифікації
        if (loggedInUser == null) {
            return "redirect:/";
        }

        // Перевірка прав доступу
        if (!authService.hasDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        // Отримання існуючого пацієнта
        PatientModel existingPatient = patientRepo.findById(patientId).orElse(null);
        if (existingPatient == null) {
            return "redirect:/error";
        }

        // Додаткова перевірка прав
        if (authService.isDoctor(loggedInUser) && !loggedInUser.getId().equals(existingPatient.getDoctor().getId())) {
            return "redirect:/access-denied";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", loggedInUser);
            model.addAttribute("doctors", personalRepo.findAll());
            model.addAttribute("patientId", patientId);
            return "edit-patient";
        }

        // Перевірка вибраного лікаря
        Long doctorId = patient.getDoctor() != null ? patient.getDoctor().getId() : null;
        if (doctorId == null || !personalRepo.existsById(doctorId)) {
            bindingResult.rejectValue("doctor", "error.patient", "Лікар не обраний або не існує.");
            model.addAttribute("user", loggedInUser);
            model.addAttribute("doctors", personalRepo.findAll());
            model.addAttribute("patientId", patientId);
            return "edit-patient";
        }

        PersonalModel doctor = personalRepo.findById(doctorId).orElse(null);

        // Оновлення даних пацієнта
        existingPatient.setFullName(patient.getFullName());
        existingPatient.setPhone(patient.getPhone());
        existingPatient.setDiagnosis(patient.getDiagnosis());
        existingPatient.setBirthDate(patient.getBirthDate());
        existingPatient.setWard(patient.getWard());
        existingPatient.setBed(patient.getBed());
        existingPatient.setDepartment(patient.getDepartment());
        existingPatient.setNotes(patient.getNotes());
        existingPatient.setAppointmentDateFrom(patient.getAppointmentDateFrom());
        existingPatient.setAppointmentDateTo(patient.getAppointmentDateTo());
        existingPatient.setDoctor(doctor);

        patientRepo.save(existingPatient);

        redirectAttributes.addFlashAttribute("successMessage",
                "Дані пацієнта " + existingPatient.getFullName() + " успішно оновлено!");

        // Редірект в залежності від ролі користувача
        if (authService.isMainDoctor(loggedInUser)) {
            return "redirect:/AllReview";
        } else {
            return "redirect:/DoctorHome/dashboard/" + loggedInUser.getId();
        }
    }
}