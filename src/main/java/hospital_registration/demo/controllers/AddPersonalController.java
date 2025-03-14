package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.PersonalRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AddPersonalController {

    @Autowired
    private PersonalRepo personalRepo;

    @GetMapping("/addPersonal")
    public String showAddPersonalForm(Model model) {
        model.addAttribute("person", new PersonalModel()); // Виправлено ім'я атрибута
        return "/addPersonal";
    }

    @PostMapping("/addPersonal")
    public String addPersonal(
            @Valid @ModelAttribute("person") PersonalModel person,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "/addPersonal";
        }

        // Перевірка на унікальність логіна
        Optional<PersonalModel> existingPerson = personalRepo.findByLogin(person.getLogin());
        if (existingPerson.isPresent()) {
            model.addAttribute("errorMessage", "Цей логін вже існує. Виберіть інший логін.");
            return "/addPersonal"; // Повертаємо на ту саму сторінку з повідомленням
        }

        // Якщо логін унікальний, зберігаємо персонал
        personalRepo.save(person);
        redirectAttributes.addFlashAttribute("successMessage", "Медичний персонал успішно додано!");
        return "redirect:/addPersonal"; // Виправлено шлях
    }
}
