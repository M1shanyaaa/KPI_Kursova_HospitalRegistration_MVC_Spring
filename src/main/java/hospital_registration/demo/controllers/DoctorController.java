package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.service.AuthorizationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контролер для головної сторінки лікаря.
 * Перевіряє автентифікацію та доступ користувача з роллю лікаря.
 */
@Controller
public class DoctorController {

    @Autowired
    private AuthorizationService authService;

    /**
     * Відображає домашню сторінку для користувача з правами лікаря.
     *
     * @param model   модель для передачі даних до представлення
     * @param session HTTP-сесія для отримання автентифікованого користувача
     * @return сторінка "home" або редірект на логін/відмову в доступі
     */
    @GetMapping("/DoctorHome")
    public String doctorHome(Model model, HttpSession session) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/"; // Перенаправлення на логін, якщо користувач не автентифікований
        }

        if (!authService.hasDoctorAccess(user)) {
            return "redirect:/access-denied"; // Перенаправлення на сторінку відмови в доступі
        }

        model.addAttribute("user", user);
        return "home";
    }
}
