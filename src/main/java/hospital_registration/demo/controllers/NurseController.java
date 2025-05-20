package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.service.AuthorizationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контролер для обробки запитів на головну сторінку медсестри.
 */
@Controller
public class NurseController {

    @Autowired
    private AuthorizationService authService;

    /**
     * Відображає головну сторінку медсестри.
     * Перевіряє автентифікацію користувача та його права доступу.
     *
     * @param model   модель для передачі даних у представлення
     * @param session HTTP-сесія для отримання інформації про автентифікованого користувача
     * @return назву шаблону сторінки або редірект при відсутності авторизації
     */
    @GetMapping("/NurseHome")
    public String nurseHome(Model model, HttpSession session) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");

        // Якщо користувач не автентифікований — редірект на сторінку логіну
        if (user == null) {
            return "redirect:/";
        }

        // Якщо користувач не має прав медсестри — редірект на сторінку відмови в доступі
        if (!authService.hasNurseAccess(user)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("user", user);
        return "home";
    }
}
