package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контролер для обробки сторінки відмови в доступі.
 * Відображає повідомлення, якщо користувач не має потрібних прав доступу.
 */
@Controller
public class ErrorController {

    /**
     * Обробляє запит до сторінки "/access-denied".
     * Перевіряє автентифікацію користувача та передає його дані до моделі.
     *
     * @param model   модель для передачі даних у представлення
     * @param session HTTP-сесія для отримання автентифікованого користувача
     * @return сторінка "access-denied" або редірект на головну сторінку
     */
    @GetMapping("/access-denied")
    public String accessDenied(Model model, HttpSession session) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);
        return "access-denied"; // сторінка з повідомленням про відмову в доступі
    }
}
