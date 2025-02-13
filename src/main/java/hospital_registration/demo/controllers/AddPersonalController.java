package hospital_registration.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AddPersonalController {


    @GetMapping("/addpersonal")
    public String forex(Model model) {
        return "/addpersonal";
    }
}
