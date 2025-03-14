//package hospital_registration.demo.controllers;
//
//import hospital_registration.demo.Models.DefoltDoctorModel;
//import hospital_registration.demo.repo.DefoltDoctorRepo;
//import jakarta.validation.Valid;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//
//@Controller
//public class AddPersonalController {
//
//    private final DefoltDoctorRepo doctorRepo;
//
//    public AddPersonalController(DefoltDoctorRepo doctorRepo) {
//        this.doctorRepo = doctorRepo;
//    }
//
//    @GetMapping("/addpersonal")
//    public String showAddPersonalForm(Model model) {
//        model.addAttribute("doctor", new DefoltDoctorModel());
//        return "addpersonal";
//    }
//
//    @PostMapping("/addDoctor")
//    public String addDoctor(@Valid @ModelAttribute("doctor") DefoltDoctorModel doctor,
//                            BindingResult bindingResult,
//                            Model model) {
//        if (bindingResult.hasErrors()) {
//            return "addpersonal"; // Якщо є помилки, повертаємо форму з повідомленнями
//        }
//        doctorRepo.save(doctor);
//        model.addAttribute("successMessage", "Лікаря успішно додано!");
//        return "redirect:/addpersonal";
//    }
//
//
//
//}
//
