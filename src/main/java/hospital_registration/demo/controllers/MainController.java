package hospital_registration.demo.controllers;

import hospital_registration.demo.repo.PersonalRepo;
import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.service.AuthorizationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Контролер для обробки запитів на головну сторінку,
 * автентифікації користувачів та виходу із системи.
 */
@Controller
public class MainController {

    private final PersonalRepo personalRepo;
    private final AuthorizationService authService;

    /**
     * Конструктор контролера з інжекцією залежностей.
     *
     * @param personalRepo репозиторій персоналу
     * @param authService сервіс авторизації
     */
    @Autowired
    public MainController(PersonalRepo personalRepo, AuthorizationService authService) {
        this.personalRepo = personalRepo;
        this.authService = authService;
    }

    /**
     * Відображає сторінку входу в систему.
     *
     * @param error повідомлення про помилку, якщо таке є
     * @param model модель для передачі атрибутів у представлення
     * @return ім'я шаблону сторінки входу
     */
    @GetMapping("/")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Невірний логін або пароль");
        }
        return "login-page";
    }

    /**
     * Обробляє вхід користувача.
     * Перевіряє логін та пароль, зберігає користувача в сесії,
     * та перенаправляє відповідно до його ролі.
     *
     * @param maindoctor об'єкт персоналу з введеними даними
     * @param model модель для передачі атрибутів
     * @param session HTTP-сесія для зберігання авторизованого користувача
     * @return редірект на домашню сторінку або назад на вхід при помилці
     */
    @PostMapping("/")
    public String login(@ModelAttribute PersonalModel maindoctor, Model model, HttpSession session) {
        Optional<PersonalModel> userOptional = personalRepo.findByLogin(maindoctor.getLogin());

        if (userOptional.isPresent() &&
                userOptional.get().getAccess_key().equals(maindoctor.getAccess_key())) {

            PersonalModel loggedInUser = userOptional.get();
            session.setAttribute("loggedInUser", loggedInUser);

            // Перенаправлення відповідно до ролі
            if (authService.isMainDoctor(loggedInUser)) {
                return "redirect:/MainDoctorHome";
            } else if (authService.isDoctor(loggedInUser)) {
                return "redirect:/DoctorHome";
            } else if (authService.isNurse(loggedInUser)) {
                return "redirect:/NurseHome";
            }
        }

        // Невдала автентифікація
        return "redirect:/?error=true";
    }

    /**
     * Вихід користувача з системи.
     * Очищує сесію і перенаправляє на сторінку входу.
     *
     * @param session HTTP-сесія для завершення
     * @return редірект на сторінку входу
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
