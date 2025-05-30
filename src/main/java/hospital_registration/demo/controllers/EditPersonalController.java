package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.PersonalRepo;
import hospital_registration.demo.service.AuthorizationService;
import hospital_registration.demo.service.PersonalValidationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контролер для керування персоналом лікарні.
 * Дозволяє головному лікарю переглядати та видаляти співробітників.
 */
@Controller
public class EditPersonalController {

    @Autowired
    private PersonalRepo personalRepo;

    @Autowired
    private AuthorizationService authService;

    @Autowired
    PersonalValidationService validationService;

    /**
     * Відображає таблицю всіх співробітників для головного лікаря.
     * Головний лікар не може редагувати сам себе.
     *
     * @param session HTTP-сесія для перевірки користувача
     * @param searchTerm термін для пошуку (опціонально)
     * @param searchType тип пошуку (опціонально)
     * @param model модель для передачі атрибутів у представлення
     * @return назва шаблону або редірект при відсутності доступу
     */
    @GetMapping("/editPersinal")
    public String getPersonalManagement(HttpSession session,
                                        @RequestParam(value = "search", required = false) String searchTerm,
                                        @RequestParam(value = "searchType", required = false, defaultValue = "all") String searchType,
                                        Model model) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }

        // Тільки головний лікар може керувати персоналом
        if (!authService.isMainDoctor(user)) {
            return "redirect:/access-denied";
        }

        // Отримуємо всіх співробітників, крім поточного користувача
        List<PersonalModel> allPersonal = personalRepo.findAll()
                .stream()
                .filter(p -> !p.getId().equals(user.getId()))
                .collect(Collectors.toList());

        // Фільтруємо за пошуковим запитом
        List<PersonalModel> filteredPersonal = getFilteredPersonal(allPersonal, searchTerm, searchType);

        model.addAttribute("user", user);
        model.addAttribute("personalList", filteredPersonal);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("searchType", searchType);
        model.addAttribute("totalPersonal", filteredPersonal.size());

        return "edit-personal";
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/personal/update")
    public String updatePersonal(@Valid @ModelAttribute("person") PersonalModel person,
                                 BindingResult bindingResult,
                                 @RequestParam(required = false) String password,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !authService.isMainDoctor(loggedInUser)) {
            return "redirect:/";
        }

        // Перевірка наявності персоналу
        PersonalModel existing = personalRepo.findById(person.getId()).orElse(null);
        if (existing == null) {
            redirectAttributes.addFlashAttribute("error", "Співробітника не знайдено!");
            return "redirect:/editPersonal";
        }

        // Валідація (з урахуванням ID для унікальності login/email)
        validationService.validatePersonal(person, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", loggedInUser);
            model.addAttribute("person", person);
            return "editPersonal";
        }

        // Копіювання полів, крім пароля, якщо порожній
        existing.setFullName(person.getFullName());
        existing.setLogin(person.getLogin());
        existing.setPosition(person.getPosition());
        existing.setSpecialty(person.getSpecialty());
        existing.setEmail(person.getEmail());
        existing.setPhone(person.getPhone());

        if (password != null && !password.trim().isEmpty()) {
            existing.setAccess_key(password); // додай хешування, якщо потрібно
        }

        personalRepo.save(existing);
        redirectAttributes.addFlashAttribute("message", "Інформацію оновлено успішно!");

        return "redirect:/editPersonal";
    }



    /**
     * Видаляє співробітника з системи.
     * Тільки головний лікар може видаляти персонал.
     *
     * @param personalId ідентифікатор співробітника для видалення
     * @param session HTTP-сесія для перевірки користувача
     * @param redirectAttributes об'єкт для передачі повідомлень при редіректі
     * @return редірект на сторінку керування персоналом
     */

    @PostMapping("/personal/delete")
    public String deletePersonal(@RequestParam Long personalId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }

        // Тільки головний лікар може видаляти персонал
        if (!authService.isMainDoctor(user)) {
            return "redirect:/access-denied";
        }

        // Перевіряємо, що користувач не намагається видалити сам себе
        if (personalId.equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Ви не можете видалити самого себе!");
            return "redirect:/editPersinal";
        }

        try {
            PersonalModel personalToDelete = personalRepo.findById(personalId).orElse(null);
            if (personalToDelete != null) {
                String deletedName = personalToDelete.getFullName();
                personalRepo.delete(personalToDelete);
                redirectAttributes.addFlashAttribute("message",
                        "Співробітника " + deletedName + " було успішно видалено з системи.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Співробітника не знайдено!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Помилка при видаленні співробітника. Спробуйте ще раз.");
        }

        return "redirect:/editPersinal";
    }

    /**
     * Фільтрує список персоналу за пошуковим запитом та типом пошуку.
     *
     * @param allPersonal повний список персоналу
     * @param searchTerm термін для пошуку
     * @param searchType тип пошуку (name, login, position, specialty, email, all)
     * @return відфільтрований список персоналу
     */
    private List<PersonalModel> getFilteredPersonal(List<PersonalModel> allPersonal, String searchTerm, String searchType) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return allPersonal;
        }

        String cleanSearchTerm = searchTerm.trim().toLowerCase();

        return allPersonal.stream()
                .filter(personal -> {
                    switch (searchType) {
                        case "name":
                            return personal.getFullName().toLowerCase().contains(cleanSearchTerm);
                        case "login":
                            return personal.getLogin().toLowerCase().contains(cleanSearchTerm);
                        case "position":
                            return personal.getPosition().toLowerCase().contains(cleanSearchTerm);
                        case "specialty":
                            return personal.getSpecialty().toLowerCase().contains(cleanSearchTerm);
                        case "email":
                            return personal.getEmail().toLowerCase().contains(cleanSearchTerm);
                        case "phone":
                            return personal.getPhone().toString().contains(cleanSearchTerm);
                        case "all":
                        default:
                            return personal.getFullName().toLowerCase().contains(cleanSearchTerm) ||
                                    personal.getLogin().toLowerCase().contains(cleanSearchTerm) ||
                                    personal.getPosition().toLowerCase().contains(cleanSearchTerm) ||
                                    personal.getSpecialty().toLowerCase().contains(cleanSearchTerm) ||
                                    personal.getEmail().toLowerCase().contains(cleanSearchTerm) ||
                                    personal.getPhone().toString().contains(cleanSearchTerm);
                    }
                })
                .collect(Collectors.toList());
    }
}