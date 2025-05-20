package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.PersonalRepo;
import hospital_registration.demo.service.AuthorizationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Контролер для обробки перегляду акаунтів користувачів.
 */
@Controller
public class AccountController {

    @Autowired
    private PersonalRepo personalRepo;

    @Autowired
    private AuthorizationService authService;

    /**
     * Відображає сторінку особистого акаунта для поточного авторизованого користувача.
     *
     * @param session HTTP-сесія для отримання інформації про користувача
     * @param model   модель для передачі даних у представлення
     * @return сторінка акаунта або редірект на головну сторінку, якщо користувач не авторизований
     */
    @GetMapping("/account")
    public String account(HttpSession session, Model model) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("isOwnAccount", true);
        return "account";
    }

    /**
     * Відображає акаунт іншого користувача (працівника) для головного лікаря.
     *
     * @param id      ID користувача, акаунт якого потрібно переглянути
     * @param session HTTP-сесія для перевірки авторизації
     * @param model   модель для передачі даних у представлення
     * @return сторінка акаунта, сторінка помилки або сторінка відмови в доступі
     */
    @GetMapping("/account/{id}")
    public String viewAccount(@PathVariable Long id, HttpSession session, Model model) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        if (!authService.hasMainDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        PersonalModel user = personalRepo.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/error";
        }

        boolean isOwnAccount = loggedInUser.getId().equals(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("isOwnAccount", isOwnAccount);
        return "account";
    }
}
