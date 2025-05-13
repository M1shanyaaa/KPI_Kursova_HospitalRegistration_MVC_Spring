package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

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