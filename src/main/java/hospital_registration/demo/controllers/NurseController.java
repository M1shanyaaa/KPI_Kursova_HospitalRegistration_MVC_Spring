package hospital_registration.demo.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class NurseController {

    @GetMapping("/forex")
    public String forex(Model model) {
        return "forexfile";
    }
}
