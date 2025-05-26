package hospital_registration.demo.controllers;

import hospital_registration.demo.Models.HistoryPatientsModel;
import hospital_registration.demo.Models.PatientModel;
import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.HistoryPatientRepo;
import hospital_registration.demo.repo.PatientRepo;
import hospital_registration.demo.service.AuthorizationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Контролер для перегляду та керування інформацією про пацієнтів для лікарів.
 * Реалізує можливість перегляду пацієнтів, редагування дати виписки,
 * видалення пацієнтів і перегляд усіх пацієнтів для головного лікаря.
 */
@Controller
public class PatientReviewController {

    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private AuthorizationService authService;
    @Autowired
    private HistoryPatientRepo historyPatientRepo;


    /**
     * Відображає інформаційну панель лікаря з переліком його пацієнтів.
     * Перевіряє, чи користувач автентифікований та має права лікаря.
     * Лікар може переглядати лише своїх пацієнтів.
     *
     * @param id      ідентифікатор лікаря
     * @param session HTTP-сесія для отримання авторизованого користувача
     * @param model   модель для передачі атрибутів у представлення
     * @return назва шаблону інформаційної панелі лікаря або редірект при відсутності доступу
     */
    @GetMapping("/DoctorHome/dashboard/{id}")
    public String getDoctorDashboard(@PathVariable Long id, HttpSession session,
                                     @RequestParam(value = "search", required = false) String searchTerm,
                                     @RequestParam(value = "searchType", required = false, defaultValue = "all") String searchType,Model model) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }
        // Перевірка прав доступу
        if (!authService.hasDoctorAccess(user)) {
            return "redirect:/access-denied";
        }

        // Додаткова перевірка: лікар може бачити тільки своїх пацієнтів
        if (authService.isDoctor(user) && !user.getId().equals(id)) {
            return "redirect:/access-denied";
        }
        // Визначаємо чи є пошуковий запит
        boolean hasSearchTerm = searchTerm != null && !searchTerm.trim().isEmpty();
        String cleanSearchTerm = hasSearchTerm ? searchTerm.trim() : "";

        List<PatientModel> patients = getFilteredPatientsForDoctor(cleanSearchTerm, searchType, id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        model.addAttribute("user", user);
        model.addAttribute("patients", patients);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("searchType", searchType);
        model.addAttribute("totalPatients", patients.size());
        model.addAttribute("doctorId", id);


        return "patients-review-dashboard";
    }

    /**
     * Відображає форму для редагування дати виписки пацієнта.
     * Доступна лише лікарю, який відповідає за пацієнта.
     *
     * @param patientId ідентифікатор пацієнта
     * @param session   HTTP-сесія для перевірки користувача
     * @param model     модель для передачі даних у представлення
     * @return назва шаблону форми або редірект при відсутності доступу
     */
    // Форма для редагування дати виписки
    @GetMapping("/patients/discharge/{patientId}")
    public String getDischargeForm(@PathVariable Long patientId, HttpSession session, Model model) {
        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        // Перевірка прав доступу
        if (!authService.hasDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        PatientModel patient = patientRepo.findById(patientId).orElseThrow();

        // Лікар може редагувати тільки своїх пацієнтів
        if (authService.isDoctor(loggedInUser) && !loggedInUser.getId().equals(patient.getDoctor().getId())) {
            return "redirect:/access-denied";
        }

        model.addAttribute("patient", patient);
        model.addAttribute("user", loggedInUser);
        return "discharge-form";
    }


    /**
     * Обробляє POST-запит на оновлення дати виписки пацієнта.
     * Перевіряє права доступу лікаря і оновлює дату в базі.
     *
     * @param patientId     ідентифікатор пацієнта
     * @param dischargeDate нова дата виписки (у форматі ISO)
     * @param session       HTTP-сесія для перевірки користувача
     * @param redirectAttributes об'єкт для передачі повідомлень при редіректі
     * @return редірект на інформаційну панель лікаря
     */

    // Обробка збереження нової дати виписки
    @PostMapping("/patients/discharge/update")
    public String updateDischargeDate(@RequestParam Long patientId,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dischargeDate,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {

        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        // Перевірка прав доступу
        if (!authService.hasDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        PatientModel patient = patientRepo.findById(patientId).orElseThrow();

        // Лікар може оновлювати тільки своїх пацієнтів
        if (authService.isDoctor(loggedInUser) && !loggedInUser.getId().equals(patient.getDoctor().getId())) {
            return "redirect:/access-denied";
        }

        patient.setAppointmentDateTo(dischargeDate);
        patientRepo.save(patient);
        redirectAttributes.addFlashAttribute("message", "Дата виписки оновлена для пацієнта " + patient.getFullName());
        return "redirect:/DoctorHome/dashboard/" + patient.getDoctor().getId();
    }

    /**
     * Обробляє видалення пацієнта (виписку) лікарем.
     * Пацієнт зберігається в історії виписаних, а потім видаляється з поточних.
     *
     * @param patientId ідентифікатор пацієнта для видалення
     * @param session   HTTP-сесія для перевірки користувача
     * @param redirectAttributes об'єкт для передачі повідомлень при редіректі
     * @return редірект на інформаційну панель лікаря
     */

    @PostMapping("/patients/delete")
    public String deletePatient(@RequestParam Long patientId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        PersonalModel loggedInUser = (PersonalModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        // Перевірка прав доступу
        if (!authService.hasDoctorAccess(loggedInUser)) {
            return "redirect:/access-denied";
        }

        PatientModel patient = patientRepo.findById(patientId).orElseThrow();

        // Лікар може видаляти тільки своїх пацієнтів
        if (authService.isDoctor(loggedInUser) && !loggedInUser.getId().equals(patient.getDoctor().getId())) {
            return "redirect:/access-denied";
        }

        HistoryPatientsModel pastPatient = new HistoryPatientsModel(
                patient.getFullName(),
                patient.getPhone(),
                patient.getDiagnosis(),
                patient.getBirthDate(),
                patient.getWard(),
                patient.getDoctor(),
                patient.getNotes(),
                patient.getDepartment(),
                patient.getAppointmentDateFrom(),
                patient.getAppointmentDateTo(),
                patient.getBed()
        );
        Long doctorId = patient.getDoctor().getId();
        historyPatientRepo.save(pastPatient);
        patientRepo.delete(patient);
        redirectAttributes.addFlashAttribute("message", "Пацієнта " + patient.getFullName() + " було виписано (видалено).");
        return "redirect:/DoctorHome/dashboard/" + doctorId;
    }

    /**
     * Відображає список усіх пацієнтів для головного лікаря та лікарів.
     * Медсестри бачать іншу сторінку з пацієнтами.
     *
     * @param session HTTP-сесія для отримання користувача
     * @param model   модель для передачі атрибутів у представлення
     * @return назва шаблону зі списком пацієнтів або редірект при відсутності доступу
     */

    @GetMapping("/AllReview")
    public String getAllPatientsForMainDoctor(HttpSession session,
                                              @RequestParam(value = "search", required = false) String searchTerm,
                                              @RequestParam(value = "searchType", required = false, defaultValue = "all") String searchType,
                                              Model model) {
        PersonalModel user = (PersonalModel) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }
        List<PatientModel> allPatients = patientRepo.findAll();
        // Визначаємо чи є пошуковий запит
        boolean hasSearchTerm = searchTerm != null && !searchTerm.trim().isEmpty();
        String cleanSearchTerm = hasSearchTerm ? searchTerm.trim() : "";

        List<PatientModel> patients = getFilteredPatients(cleanSearchTerm, searchType);
        model.addAttribute("user", user);
        model.addAttribute("patients", patients);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("searchType", searchType);
        model.addAttribute("totalPatients", patients.size());
        if (authService.isDoctor(user) || authService.isMainDoctor(user) ) {
            return "patients-allreview";
        } else if (authService.isNurse(user)) {
            return "patients-allreview-nurse";
        }else{return "redirect:/error";}

    }

    /**
     * Повертає відфільтрований список пацієнтів, прикріплених до певного лікаря,
     * згідно з вказаним типом пошуку та пошуковим терміном.
     *
     * @param searchTerm термін для пошуку (ПІБ, телефон, діагноз або дата)
     * @param searchType тип пошуку:
     *                   <ul>
     *                      <li><b>name</b> — пошук за повним ім’ям пацієнта</li>
     *                      <li><b>phone</b> — пошук за номером телефону</li>
     *                      <li><b>diagnosis</b> — пошук за діагнозом</li>
     *                      <li><b>dischargeDATE</b> — пошук за датою виписки (формат dd.MM.yyyy)</li>
     *                      <li><b>recordedDATE</b> — пошук за датою реєстрації (формат dd.MM.yyyy)</li>
     *                      <li><b>all</b> або інше — універсальний пошук по всіх полях</li>
     *                   </ul>
     * @param id ідентифікатор лікаря
     * @return список пацієнтів, які відповідають умовам фільтрації; якщо пошукова дата недійсна — повертається порожній список
     */
    private List<PatientModel> getFilteredPatientsForDoctor(String searchTerm, String searchType, Long id) {
        switch (searchType) {
            case "name":
                return patientRepo.findByDoctorIdAndFullNameContainingIgnoreCase(id, searchTerm);
            case "phone":
                return patientRepo.findByDoctorIdAndPhoneContaining(id, searchTerm);
            case "diagnosis":
                return patientRepo.findByDoctorIdAndDiagnosisContainingIgnoreCase(id, searchTerm);
            case "dischargeDATE":
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate date = LocalDate.parse(searchTerm, formatter);
                    LocalDateTime from = date.atStartOfDay();
                    LocalDateTime to = date.atTime(LocalTime.MAX);
                    return patientRepo.findByAppointmentDateBetweenDoctorId(id, from, to);
                } catch (DateTimeParseException e) {
                    // Якщо введено не дату — повернути порожній список або всі записи, або логувати помилку
                    return new ArrayList<>();
                }
            case "recordedDATE":
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate date = LocalDate.parse(searchTerm, formatter);
                    LocalDateTime from = date.atStartOfDay();
                    LocalDateTime to = date.atTime(LocalTime.MAX);
                    return patientRepo.findByRecordedDateDoctor(id, from, to);
                } catch (DateTimeParseException e) {
                    // Якщо введено не дату — повернути порожній список або всі записи, або логувати помилку
                    return new ArrayList<>();
                }
            case "all":
            default:
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate date = LocalDate.parse(searchTerm, formatter);
                    LocalDateTime from = date.atStartOfDay();
                    LocalDateTime to = date.atTime(LocalTime.MAX);
                    return patientRepo.findByDateFieldsDoctor(id, from, to);
                } catch (DateTimeParseException e) {
                    return patientRepo.findByDoctorIdAndAllFieldsContaining(id, searchTerm);
                }
        }
    }


    /**
     * Повертає відфільтрований список усіх пацієнтів у системі (незалежно від лікаря),
     * згідно з вказаним типом пошуку та пошуковим терміном.
     *
     * @param searchTerm термін для пошуку (ПІБ, телефон, діагноз або дата)
     * @param searchType тип пошуку:
     *                   <ul>
     *                      <li><b>name</b> — пошук за повним ім’ям пацієнта</li>
     *                      <li><b>phone</b> — пошук за номером телефону</li>
     *                      <li><b>diagnosis</b> — пошук за діагнозом</li>
     *                      <li><b>dischargeDATE</b> — пошук за датою виписки (формат dd.MM.yyyy)</li>
     *                      <li><b>recordedDATE</b> — пошук за датою реєстрації (формат dd.MM.yyyy)</li>
     *                      <li><b>all</b> або інше — універсальний пошук по всіх полях</li>
     *                   </ul>
     * @return список пацієнтів, які відповідають умовам фільтрації; якщо пошукова дата недійсна — повертається порожній список
     */
    private List<PatientModel> getFilteredPatients(String searchTerm, String searchType) {
        switch (searchType) {
            case "name":
                return patientRepo.findByFullNameContainingIgnoreCase(searchTerm);
            case "phone":
                return patientRepo.findByPhoneContaining(searchTerm);
            case "diagnosis":
                return patientRepo.findByDiagnosisContainingIgnoreCase(searchTerm);
            case "dischargeDATE":
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate date = LocalDate.parse(searchTerm, formatter);
                    LocalDateTime from = date.atStartOfDay();
                    LocalDateTime to = date.atTime(LocalTime.MAX);
                    return patientRepo.findByAppointmentDateBetween(from, to);
                } catch (DateTimeParseException e) {
                    // Якщо введено не дату — повернути порожній список або всі записи, або логувати помилку
                    return new ArrayList<>();
                }
            case "recordedDATE":
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate date = LocalDate.parse(searchTerm, formatter);
                    LocalDateTime from = date.atStartOfDay();
                    LocalDateTime to = date.atTime(LocalTime.MAX);
                    return patientRepo.findByRecordedDate(from, to);
                } catch (DateTimeParseException e) {
                    // Якщо введено не дату — повернути порожній список або всі записи, або логувати помилку
                    return new ArrayList<>();
                }
            case "all":
            default:
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate date = LocalDate.parse(searchTerm, formatter);
                    LocalDateTime from = date.atStartOfDay();
                    LocalDateTime to = date.atTime(LocalTime.MAX);
                    return patientRepo.findByDateFields(from, to);
                } catch (DateTimeParseException e) {
                    return patientRepo.findByAllFieldsContaining(searchTerm);
                }
        }
    }
}
