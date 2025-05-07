package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.Announcement;
import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.AnnouncementRepository;
import hospital_registration.demo.repo.PersonalRepo;
import hospital_registration.demo.service.SmsService;
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

    @Autowired
    private PersonalRepo personalRepo;

    @Autowired
    private SmsService smsService;

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

        String subject = "Нове оголошення: " + announcement.getTitle();
        String message = announcement.getContent();

        List<PersonalModel> staff = personalRepo.findAll();
        for (PersonalModel person : staff) {
            try {
                smsService.sendEmail(person.getEmail(), subject, message);
            } catch (Exception e) {
                System.out.println("Помилка для " + person.getEmail() + ": " + e.getMessage());
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Оголошення додано та email розіслано.");
        return "redirect:/addAnnouncement";
    }

    @GetMapping("/listAnnouncement")
    public String showListAnnouncement(Model model, HttpSession session) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }

        List<Announcement> announcements = announcementRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("announcements", announcements);
        return "announ-list";
    }
}
