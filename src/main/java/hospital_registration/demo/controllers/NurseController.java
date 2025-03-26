package hospital_registration.demo.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NurseController {


    @GetMapping("/NurseHome")
    public String NurseHome(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/"; // Redirect to login if user is not authenticated
        }
        return "NurseHome";
    }

}
