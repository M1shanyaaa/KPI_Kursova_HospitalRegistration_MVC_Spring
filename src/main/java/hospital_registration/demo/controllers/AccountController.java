package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.PersonalRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AccountController {

    @Autowired
    private PersonalRepo personalRepo;

    // Власний акаунт
    @GetMapping("/account")
    public String account(HttpSession session, Model model) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("isOwnAccount", true);
        return "account";
    }

    // Акаунт іншого працівника
    @GetMapping("/account/{id}")
    public String viewAccount(@PathVariable Long id, HttpSession session, Model model) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        PersonalModel user = personalRepo.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/error"; // або своя кастомна сторінка
        }

        boolean isOwnAccount = loggedInUser.getId().equals(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("isOwnAccount", isOwnAccount);
        return "account";
    }
}
