package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.Announcement;
import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.AnnouncementRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
public class AddAnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @GetMapping("/addAnnouncement")
    public String showAddAnnouncementForm(Model model) {
        model.addAttribute("announcement", new Announcement());
        return "announ-form";
    }

    @PostMapping("/addAnnouncement")
    public String addAnnouncement(
            @Valid @ModelAttribute("announcement") Announcement announcement,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "announ-form";
        }

        announcementRepository.save(announcement);
        redirectAttributes.addFlashAttribute("successMessage", "Оголошення успішно додано");
        return "redirect:/addAnnouncement";
    }

    @GetMapping("/listAnnouncement")
    public String showListAnnouncement(Model model, HttpSession session) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/";
        }
        List<Announcement> announcements = announcementRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("announcements", announcements);
        return "announ-list";
    }
}
