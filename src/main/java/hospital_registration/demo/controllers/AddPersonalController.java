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

/**
 * Контролер для додавання нового медичного персоналу.
 * Доступ дозволено лише головному лікарю.
 */
@Controller
public class AddPersonalController {

    @Autowired
    private PersonalRepo personalRepo;

    @Autowired
    private AuthorizationService authService;

    /**
     * Відображає форму для створення нового працівника лікарні.
     * Перевіряє, чи користувач має права головного лікаря.
     *
     * @param model   модель для передачі даних до представлення
     * @param session HTTP-сесія для ідентифікації користувача
     * @return сторінка з формою або редірект на сторінку доступу
     */
    @GetMapping("/addPersonal")
    public String showAddPersonalForm(Model model, HttpSession session) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        if (!authService.hasMainDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("person", new PersonalModel());
        model.addAttribute("user", loggedInUser);
        return "addPersonal";
    }

    /**
     * Обробляє надсилання форми для додавання нового працівника.
     * Перевіряє валідацію даних і унікальність логіна.
     *
     * @param person             новий об'єкт персоналу, заповнений із форми
     * @param bindingResult      результат валідації
     * @param redirectAttributes повідомлення після редіректу
     * @param session            HTTP-сесія для перевірки доступу
     * @param model              модель для передачі даних у представлення
     * @return редірект на сторінку успіху або форма з помилками
     */
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

        if (!authService.hasMainDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", loggedInUser);
            return "addPersonal";
        }

        // Перевірка, чи логін уже зайнятий
        Optional<PersonalModel> existingPerson = personalRepo.findByLogin(person.getLogin());
        if (existingPerson.isPresent()) {
            model.addAttribute("errorMessage", "Цей логін вже існує. Виберіть інший логін.");
            model.addAttribute("user", loggedInUser);
            return "addPersonal";
        }

        // Збереження нового працівника
        personalRepo.save(person);
        redirectAttributes.addFlashAttribute("successMessage", "Медичний персонал успішно додано!");
        return "redirect:/addPersonal";
    }
}

