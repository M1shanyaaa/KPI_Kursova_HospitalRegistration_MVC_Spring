package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DoctorController {

    @GetMapping("/DoctorHome")
    public String NurseHome(Model model, HttpSession session) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        return "home";
    }

}
