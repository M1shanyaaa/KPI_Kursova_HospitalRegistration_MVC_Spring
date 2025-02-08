package hospital_registration.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Controller for handling requests to the main page.
 */
@Controller
public class MainController {

    /**
     * Displays the home page.
     *
     * @param model the model to add attributes to
     * @return the name of the home view
     */
    @GetMapping("/home")
    public String greeting(Model model) {
        return "home";
    }

    @GetMapping("/forex")
    public String forex(Model model) {
        return "forexfile";
    }
}