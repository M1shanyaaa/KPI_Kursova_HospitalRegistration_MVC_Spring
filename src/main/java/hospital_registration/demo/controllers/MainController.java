package hospital_registration.demo.controllers;

import hospital_registration.demo.repo.PersonalRepo;
import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.service.AuthorizationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for handling requests to the main page.
 */
@Controller
public class MainController {

    private final PersonalRepo personalRepo;
    private final AuthorizationService authService;

    @Autowired
    public MainController(PersonalRepo personalRepo, AuthorizationService authService) {
        this.personalRepo = personalRepo;
        this.authService = authService;
    }

    /**
     * Displays the login page.
     *
     * @param error optional error message
     * @param model the model to add attributes to
     * @return the name of the login view
     */
    @GetMapping("/")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Невірний логін або пароль");
        }
        return "login-page";
    }

    /**
     * Processes the login request.
     *
     * @param maindoctor the model object containing login information
     * @param model      the model to add attributes to
     * @param session    the HTTP session to store authenticated user
     * @return redirect to respective home page if successful, otherwise back to login with error
     */
    @PostMapping("/")
    public String login(@ModelAttribute PersonalModel maindoctor, Model model, HttpSession session) {
        Optional<PersonalModel> userOptional = personalRepo.findByLogin(maindoctor.getLogin());

        if (userOptional.isPresent() && userOptional.get().getAccess_key().equals(maindoctor.getAccess_key())) {
            PersonalModel loggedInUser = userOptional.get();
            session.setAttribute("loggedInUser", loggedInUser);

            // Перенаправлення в залежності від ролі користувача
            if (authService.isMainDoctor(loggedInUser)) {
                return "redirect:/MainDoctorHome";
            } else if (authService.isDoctor(loggedInUser)) {
                return "redirect:/DoctorHome";
            } else if (authService.isNurse(loggedInUser)) {
                return "redirect:/NurseHome";
            }
        }

        return "redirect:/?error=true"; // Автентифікація не вдалася
    }

    /**
     * Logs out the current user and invalidates the session.
     *
     * @param session the HTTP session to invalidate
     * @return redirect to login page
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/"; // Logout and redirect to login page
    }
}