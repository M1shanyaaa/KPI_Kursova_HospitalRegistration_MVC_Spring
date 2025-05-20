package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.Announcement;
import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.AnnouncementRepository;
import hospital_registration.demo.repo.PersonalRepo;
import hospital_registration.demo.service.AuthorizationService;
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

/**
 * Контролер для створення та перегляду оголошень у системі.
 */
@Controller
public class AddAnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private PersonalRepo personalRepo;

    @Autowired
    private SmsService smsService;

    @Autowired
    private AuthorizationService authService;

    /**
     * Відображає форму для створення нового оголошення.
     * Доступно лише головному лікарю.
     *
     * @param model   модель для передачі даних у шаблон
     * @param session HTTP-сесія для перевірки доступу
     * @return сторінка з формою або редірект при відсутності доступу
     */
    @GetMapping("/addAnnouncement")
    public String showAddAnnouncementForm(Model model, HttpSession session) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (!authService.hasMainDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }
        model.addAttribute("announcement", new Announcement());
        return "announ-form";
    }

    /**
     * Обробляє надсилання форми нового оголошення. Після збереження оголошення — надсилає повідомлення email усім працівникам.
     *
     * @param announcement       модель оголошення, заповнена з форми
     * @param bindingResult      результат валідації форми
     * @param redirectAttributes атрибути для передачі повідомлень між запитами
     * @param session            HTTP-сесія для перевірки доступу
     * @param model              модель для повернення до форми при помилці
     * @return редірект на форму або її повторне відображення при помилці
     */
    @PostMapping("/addAnnouncement")
    public String addAnnouncement(
            @Valid @ModelAttribute("announcement") Announcement announcement,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (!authService.hasMainDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

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

    /**
     * Відображає список усіх оголошень.
     *
     * @param model   модель для шаблону
     * @param session HTTP-сесія для перевірки авторизації
     * @return сторінка зі списком оголошень або редірект при відсутності авторизації
     */
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
