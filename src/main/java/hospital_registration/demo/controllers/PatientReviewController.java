package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.HistoryPatientsModel;
import hospital_registration.demo.Models.PatientModel;
import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.HistoryPatientRepo;
import hospital_registration.demo.repo.PatientRepo;
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

    @GetMapping("/DoctorHome/dashboard/{id}")
    public String getDoctorDashboard(@PathVariable Long id, HttpSession session, Model model) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
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
    public String getDischargeForm(@PathVariable Long patientId, Model model) {
        PatientModel patient = patientRepo.findById(patientId).orElseThrow();
        model.addAttribute("patient", patient);
        return "discharge-form";
    }

    // Обробка збереження нової дати виписки
    @PostMapping("/patients/discharge/update")
    public String updateDischargeDate(@RequestParam Long patientId,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dischargeDate,
                                      RedirectAttributes redirectAttributes) {
        PatientModel patient = patientRepo.findById(patientId).orElseThrow();
        patient.setAppointmentDateTo(dischargeDate);
        patientRepo.save(patient);
        redirectAttributes.addFlashAttribute("message", "Дата виписки оновлена для пацієнта " + patient.getFullName());
        return "redirect:/DoctorHome/dashboard/" + patient.getDoctor().getId();
    }

    @PostMapping("/patients/delete")
    public String deletePatient(@RequestParam Long patientId, RedirectAttributes redirectAttributes) {
        PatientModel patient = patientRepo.findById(patientId).orElseThrow();
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

    @GetMapping("/AllReviewMainDoctor")
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

        return "pations-review-maindoctor";
    }
}
