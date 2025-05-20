package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.service.AuthorizationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контролер для обробки запитів до головної сторінки головного лікаря.
 */
@Controller
public class MainDoctorController {

    @Autowired
    private AuthorizationService authService;

    /**
     * Обробляє запит на головну сторінку головного лікаря.
     * Перевіряє автентифікацію та авторизацію користувача.
     *
     * @param model   модель для передачі атрибутів у представлення
     * @param session поточна HTTP-сесія для отримання автентифікованого користувача
     * @return назва шаблону сторінки або редірект при відсутності доступу
     */
    @GetMapping("/MainDoctorHome")
    public String mainDoctorHome(Model model, HttpSession session) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");

        // Якщо користувач не увійшов в систему — перенаправити на логін
        if (user == null) {
            return "redirect:/";
        }

        // Якщо користувач не має прав головного лікаря — відмовити в доступі
        if (!authService.hasMainDoctorAccess(user)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("user", user);
        return "home";
    }
}
