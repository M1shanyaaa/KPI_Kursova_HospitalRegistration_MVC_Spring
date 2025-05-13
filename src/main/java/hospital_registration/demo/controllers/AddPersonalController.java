package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
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

import java.util.Optional;

@Controller
public class AddPersonalController {

    @Autowired
    private PersonalRepo personalRepo;

    @Autowired
    private AuthorizationService authService;

    @GetMapping("/addPersonal")
    public String showAddPersonalForm(Model model, HttpSession session) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        // Тільки головний лікар може додавати персонал
        if (!authService.hasMainDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("person", new PersonalModel());
        model.addAttribute("user", loggedInUser);
        return "addPersonal";
    }

    @PostMapping("/addPersonal")
    public String addPersonal(
            @Valid @ModelAttribute("person") PersonalModel person,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        // Тільки головний лікар може додавати персонал
        if (!authService.hasMainDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", loggedInUser);
            return "addPersonal";
        }

        // Перевірка на унікальність логіна
        Optional<PersonalModel> existingPerson = personalRepo.findByLogin(person.getLogin());
        if (existingPerson.isPresent()) {
            model.addAttribute("errorMessage", "Цей логін вже існує. Виберіть інший логін.");
            model.addAttribute("user", loggedInUser);
            return "addPersonal"; // Повертаємо на ту саму сторінку з повідомленням
        }

        // Якщо логін унікальний, зберігаємо персонал
        personalRepo.save(person);
        redirectAttributes.addFlashAttribute("successMessage", "Медичний персонал успішно додано!");
        return "redirect:/addPersonal";
    }
}