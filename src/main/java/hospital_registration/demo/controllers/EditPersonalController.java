package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.PersonalRepo;
import hospital_registration.demo.service.AuthorizationService;
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
import java.util.Optional;
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
    private PasswordEncoder passwordEncoder;


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
    @PostMapping("/personal/update")
    public String updatePersonal(@Valid @ModelAttribute("person") PersonalModel person,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 @RequestParam(value = "search", required = false) String searchTerm,
                                 @RequestParam(value = "searchType", required = false, defaultValue = "all") String searchType,
                                 Model model) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (user == null || !authService.isMainDoctor(user)) {
            return "redirect:/";
        }

        // Отримуємо всіх співробітників, крім поточного користувача
        List<PersonalModel> allPersonal = personalRepo.findAll()
                .stream()
                .filter(p -> !p.getId().equals(user.getId()))
                .collect(Collectors.toList());

        // Фільтруємо за пошуковим запитом
        List<PersonalModel> filteredPersonal = getFilteredPersonal(allPersonal, searchTerm, searchType);

        PersonalModel personal = personalRepo.findById(person.getId()).orElse(null);
        if (personal != null) {
            // Валідація даних
            if (person.getFullName() == null || person.getFullName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Ім'я не може бути порожнім!");
                return "redirect:/editPersinal";
            }

            if (person.getLogin() == null || person.getLogin().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Логін не може бути порожнім!");
                return "redirect:/editPersinal";
            }

            if (person.getEmail() == null || person.getEmail().trim().isEmpty() || !person.getEmail().contains("@")) {
                redirectAttributes.addFlashAttribute("error", "Некоректний формат email!");
                return "redirect:/editPersinal";
            }

            // Валідація телефону - має бути у форматі 0xxxxxxxxx
            if (person.getPhone() == null || person.getPhone().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Номер телефону не може бути порожнім!");
                return "redirect:/editPersinal";
            }

            String cleanPhone = person.getPhone().trim().replaceAll("[^0-9]", ""); // Видаляємо всі нецифрові символи

            if (!cleanPhone.matches("0\\d{9}")) {
                redirectAttributes.addFlashAttribute("error", "Телефон має бути у форматі 0xxxxxxxxx (10 цифр, починається з 0)!");
                return "redirect:/editPersinal";
            }

            // Валідація унікальності логіна
            Optional<PersonalModel> existingByLogin = personalRepo.findByLogin(person.getLogin().trim());
            if (existingByLogin.isPresent() && !existingByLogin.get().getId().equals(person.getId())) {
                redirectAttributes.addFlashAttribute("error", "Користувач з таким логіном вже існує!");
                return "redirect:/editPersinal";
            }


            // Валідація унікальності email
            Optional<PersonalModel> existingByEmail = personalRepo.findByEmail(person.getEmail().trim());
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(person.getId())) {
                redirectAttributes.addFlashAttribute("error", "Користувач з такою електронною поштою вже існує!");
                return "redirect:/editPersinal";
            }

            // Валідація унікальності телефону
            Optional<PersonalModel> existingByPhone = personalRepo.findByPhone(cleanPhone);
            if (existingByPhone.isPresent() && !existingByPhone.get().getId().equals(person.getId())) {
                redirectAttributes.addFlashAttribute("error", "Користувач з таким номером телефону вже існує!");
                return "redirect:/editPersinal";
            }

            // Оновлюємо дані
            personal.setFullName(person.getFullName().trim());
            personal.setLogin(person.getLogin().trim());
            personal.setPosition(person.getPosition().trim());
            personal.setSpecialty(person.getSpecialty().trim());
            personal.setEmail(person.getEmail().trim());
            personal.setPhone(cleanPhone); // Зберігаємо як String

            // Оновлюємо пароль тільки якщо він був введений
            if (person.getAccess_key() != null && !person.getAccess_key().trim().isEmpty()) {
                personal.setAccess_key(person.getAccess_key().trim());
            }

            personalRepo.save(personal);
            redirectAttributes.addFlashAttribute("message", "Інформацію про " + personal.getFullName() + " оновлено успішно!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Співробітника не знайдено!");
        }

        return "redirect:/editPersinal";
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
     * @param searchType тип пошуку (name, login, position, specialty, email, phone, all)
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
                            return personal.getFullName() != null &&
                                    personal.getFullName().toLowerCase().contains(cleanSearchTerm);
                        case "login":
                            return personal.getLogin() != null &&
                                    personal.getLogin().toLowerCase().contains(cleanSearchTerm);
                        case "position":
                            return personal.getPosition() != null &&
                                    personal.getPosition().toLowerCase().contains(cleanSearchTerm);
                        case "specialty":
                            return personal.getSpecialty() != null &&
                                    personal.getSpecialty().toLowerCase().contains(cleanSearchTerm);
                        case "email":
                            return personal.getEmail() != null &&
                                    personal.getEmail().toLowerCase().contains(cleanSearchTerm);
                        case "phone":
                            return personal.getPhone() != null &&
                                    personal.getPhone().contains(cleanSearchTerm);
                        case "all":
                        default:
                            return (personal.getFullName() != null && personal.getFullName().toLowerCase().contains(cleanSearchTerm)) ||
                                    (personal.getLogin() != null && personal.getLogin().toLowerCase().contains(cleanSearchTerm)) ||
                                    (personal.getPosition() != null && personal.getPosition().toLowerCase().contains(cleanSearchTerm)) ||
                                    (personal.getSpecialty() != null && personal.getSpecialty().toLowerCase().contains(cleanSearchTerm)) ||
                                    (personal.getEmail() != null && personal.getEmail().toLowerCase().contains(cleanSearchTerm)) ||
                                    (personal.getPhone() != null && personal.getPhone().contains(cleanSearchTerm));
                    }
                })
                .collect(Collectors.toList());
    }
}