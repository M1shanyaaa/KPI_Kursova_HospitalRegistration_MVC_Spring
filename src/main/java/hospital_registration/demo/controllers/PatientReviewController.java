package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.HistoryPatientsModel;
import hospital_registration.demo.Models.PatientModel;
import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.HistoryPatientRepo;
import hospital_registration.demo.repo.PatientRepo;
import hospital_registration.demo.service.AuthorizationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class PatientReviewController {

    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private HistoryPatientRepo historyPatientRepo;
    @Autowired
    private AuthorizationService authService;

    @GetMapping("/DoctorHome/dashboard/{id}")
    public String getDoctorDashboard(@PathVariable Long id, HttpSession session, Model model) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }
        // Перевірка прав доступу
        if (!authService.hasDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        // Додаткова перевірка: лікар може бачити тільки своїх пацієнтів
        if (authService.isDoctor(loggedInUser) && !loggedInUser.getId().equals(id)) {
            return "redirect:/access-denied";
        }

        List<PatientModel> patients = patientRepo.findByDoctor_Id(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        model.addAttribute("patients", patients);
        model.addAttribute("formatter", formatter);
        model.addAttribute("user", loggedInUser);

        return "pations-review-dashboard";
    }

    // Форма для редагування дати виписки
    @GetMapping("/patients/discharge/{patientId}")
    public String getDischargeForm(@PathVariable Long patientId, HttpSession session, Model model) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        // Перевірка прав доступу
        if (!authService.hasDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        PatientModel patient = patientRepo.findById(patientId).orElseThrow();

        // Лікар може редагувати тільки своїх пацієнтів
        if (authService.isDoctor(loggedInUser) && !loggedInUser.getId().equals(patient.getDoctor().getId())) {
            return "redirect:/access-denied";
        }

        model.addAttribute("patient", patient);
        model.addAttribute("user", loggedInUser);
        return "discharge-form";
    }

    // Обробка збереження нової дати виписки
    @PostMapping("/patients/discharge/update")
    public String updateDischargeDate(@RequestParam Long patientId,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dischargeDate,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {

        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        // Перевірка прав доступу
        if (!authService.hasDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        PatientModel patient = patientRepo.findById(patientId).orElseThrow();

        // Лікар може оновлювати тільки своїх пацієнтів
        if (authService.isDoctor(loggedInUser) && !loggedInUser.getId().equals(patient.getDoctor().getId())) {
            return "redirect:/access-denied";
        }

        patient.setAppointmentDateTo(dischargeDate);
        patientRepo.save(patient);
        redirectAttributes.addFlashAttribute("message", "Дата виписки оновлена для пацієнта " + patient.getFullName());
        return "redirect:/DoctorHome/dashboard/" + patient.getDoctor().getId();
    }

    @PostMapping("/patients/delete")
    public String deletePatient(@RequestParam Long patientId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        // Перевірка прав доступу
        if (!authService.hasDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        PatientModel patient = patientRepo.findById(patientId).orElseThrow();

        // Лікар може видаляти тільки своїх пацієнтів
        if (authService.isDoctor(loggedInUser) && !loggedInUser.getId().equals(patient.getDoctor().getId())) {
            return "redirect:/access-denied";
        }

        HistoryPatientsModel pastPatient = new HistoryPatientsModel(
                patient.getFullName(),
                patient.getPhone(),
                patient.getDiagnosis(),
                patient.getBirthDate(),
                patient.getWard(),
                patient.getDoctor(),
                patient.getNotes(),
                patient.getDepartment(),
                patient.getAppointmentDateFrom(),
                patient.getAppointmentDateTo(),
                patient.getBed()
        );
        Long doctorId = patient.getDoctor().getId();
        historyPatientRepo.save(pastPatient);
        patientRepo.delete(patient);
        redirectAttributes.addFlashAttribute("message", "Пацієнта " + patient.getFullName() + " було виписано (видалено).");
        return "redirect:/DoctorHome/dashboard/" + doctorId;
    }

    @GetMapping("/AllReview")
    public String getAllPatientsForMainDoctor(HttpSession session, Model model) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }


        List<PatientModel> allPatients = patientRepo.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        model.addAttribute("patients", allPatients);
        model.addAttribute("formatter", formatter);
        model.addAttribute("user", loggedInUser);

        return "pations-allreview";
    }
}
