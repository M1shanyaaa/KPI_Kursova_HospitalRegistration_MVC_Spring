package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.service.AuthorizationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NurseController {

    @Autowired
    private AuthorizationService authService;

    @GetMapping("/NurseHome")
    public String nurseHome(Model model, HttpSession session) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/"; // Перенаправлення на логін, якщо користувач не автентифікований
        }

        if (!authService.hasNurseAccess(user)) {
            return "redirect:/access-denied"; // Перенаправлення на сторінку відмови в доступі
        }

        model.addAttribute("user", user);
        return "home";
    }
}