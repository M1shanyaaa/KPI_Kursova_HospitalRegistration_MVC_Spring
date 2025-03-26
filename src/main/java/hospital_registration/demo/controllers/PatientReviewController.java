package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PatientModel;
import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.PatientRepo;
import hospital_registration.demo.repo.PersonalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PatientReviewController {

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private PersonalRepo personalRepo;

    @GetMapping("/DoctorHome/dashboard/{id}")
    public String getDoctorDashboard(Model model, @PathVariable Long id) {
        PersonalModel doctor = personalRepo.findById(id).orElseThrow();
        List<PatientModel> patients = patientRepo.findByDoctor(doctor);
        model.addAttribute("patients", patients);
        model.addAttribute("doctor", doctor);
        return "maindoctor-dashboard";
    }
}
