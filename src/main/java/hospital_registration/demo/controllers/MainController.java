package hospital_registration.demo.controllers;

import hospital_registration.demo.repo.PersonalRepo;
import hospital_registration.demo.Models.PersonalModel;
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

    @Autowired
    public MainController(PersonalRepo personalRepo) {
        this.personalRepo = personalRepo;
    }

    /**
     * Displays the home page.
     *
     * @param model   the model to add attributes to
     * @param session the HTTP session to check user authentication
     * @return the name of the home view or redirect to login if not authenticated
     */
    @GetMapping("/MainDoctorHome")
    public String home(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/"; // Redirect to login if user is not authenticated
        }
        return "home";
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
            model.addAttribute("errorMessage", "Invalid login or password");
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
        Optional<PersonalModel> loggedInUser = personalRepo.findByLogin(maindoctor.getLogin());

        if (loggedInUser.isPresent() && loggedInUser.get().getAccess_key().equals(maindoctor.getAccess_key())) {
            session.setAttribute("loggedInUser", loggedInUser.get());

            // Redirect based on user position
            String position = loggedInUser.get().getPosition();
            if ("Головний лікар".equalsIgnoreCase(position)) {
                return "redirect:/MainDoctorHome";
            } else if ("Медсестра".equalsIgnoreCase(position)) {
                return "redirect:/NurseHome";
            }else if ("Лікар".equalsIgnoreCase(position)) {
                return "redirect:/DoctorHome";
            }
        }

        return "redirect:/?error=true"; // Authentication failed
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
