package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.MainDoctorModel;
import hospital_registration.demo.repo.MainDoctorRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Controller for handling requests to the main page.
 */
@Controller
public class MainController {

    @Autowired
    private MainDoctorRepo doctorRepo;

    /**
     * Displays the home page.
     *
     * @param model the model to add attributes to
     * @return the name of the home view
     */
    @GetMapping("/MainDoctorHome")
    public String home(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/"; // Якщо користувач не увійшов, повернути на сторінку логіна
        }
        return "home";
    }

    @GetMapping("/forex")
    public String forex(Model model) {
        return "forexfile";
    }

    @GetMapping("/")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid login or password");
        }
        return "login-page";
    }

    @PostMapping("/")
    public String login(@ModelAttribute MainDoctorModel maindoctor, Model model, HttpSession session) {
        Optional<MainDoctorModel> loggedInUser = doctorRepo.findByLogin(maindoctor.getLogin());

        if (loggedInUser.isPresent() && loggedInUser.get().getAccess_key().equals(maindoctor.getAccess_key())) {
            session.setAttribute("loggedInUser", loggedInUser.get());
            return "redirect:/MainDoctorHome"; // Успішний вхід
        } else {
            return "redirect:/?error=true"; // Помилка аутентифікації
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/"; // Вихід користувача
    }
}
