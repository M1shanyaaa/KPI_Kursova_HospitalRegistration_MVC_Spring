package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.NurseModel;
import hospital_registration.demo.repo.NurseRepo;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AddNurseController {

    private final NurseRepo nurseRepo;

    public AddNurseController(NurseRepo nurseRepo) {
        this.nurseRepo = nurseRepo;
    }

    @GetMapping("/addNurse")
    public String forex(Model model) {
        model.addAttribute("nurse", new NurseModel()); // Передаємо порожній об'єкт NurseModel у форму
        return "/addNurse";
    }

    @PostMapping("/addNurse")
    public String addNurse(@Valid @ModelAttribute("nurse") NurseModel nurse,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/addNurse"; // Якщо є помилки, повертаємо ту ж саму сторінку
        }

        nurseRepo.save(nurse);
        redirectAttributes.addFlashAttribute("successMessage", "Медсестру успішно додано!");
        return "redirect:/addNurse";
    }
}
