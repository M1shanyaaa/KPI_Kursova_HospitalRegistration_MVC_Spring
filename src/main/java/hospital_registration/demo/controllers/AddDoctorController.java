package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.DefoltDoctorModel;
import hospital_registration.demo.repo.DefoltDoctorRepo;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AddDoctorController {

    private final DefoltDoctorRepo defoltDoctorRepo;

    public AddDoctorController(DefoltDoctorRepo defoltDoctorRepo) {
        this.defoltDoctorRepo = defoltDoctorRepo;
    }

    @GetMapping("/addDoctor")
    public String forex(Model model) {
        model.addAttribute("doctor", new DefoltDoctorModel()); // Передаємо порожній об'єкт у форму
        return "/addDoctor";
    }

    @PostMapping("/addDoctor")
    public String addDoctor(@Valid @ModelAttribute("doctor") DefoltDoctorModel doctor,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/addDoctor"; // Якщо є помилки, повертаємо ту ж саму сторінку
        }

        defoltDoctorRepo.save(doctor);
        redirectAttributes.addFlashAttribute("successMessage", "Лікаря успішно додано!");
        return "redirect:/addDoctor";
    }
}
