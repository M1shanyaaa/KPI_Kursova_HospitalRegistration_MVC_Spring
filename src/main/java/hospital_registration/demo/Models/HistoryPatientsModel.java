package hospital_registration.demo.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Сутність, що представляє історію пацієнта в лікарні.
 * Містить особисті дані, інформацію про лікаря, діагноз, дати перебування та інші деталі.
 */
@Entity
@Table(name = "history_patients")
public class HistoryPatientsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Повне ім'я пацієнта. Не може бути порожнім. */
    @NotBlank(message = "Ім'я не може бути порожнім")
    private String fullName;

    /** Номер телефону пацієнта. Повинен відповідати вказаному формату. */
    @NotBlank(message = "Номер телефону не може бути порожнім")
    @Pattern(regexp = "\\+?\\d{10,15}", message = "Некоректний формат телефону")
    private String phone;

    /** Діагноз пацієнта. Максимум 255 символів. */
    @NotBlank(message = "Діагноз не може бути порожнім")
    @Size(max = 255, message = "Діагноз не може перевищувати 255 символів")
    private String diagnosis;

    /** Дата народження пацієнта. */
    @NotNull(message = "Дата народження не може бути порожньою")
    private LocalDate birthDate;

    /** Номер палати, де перебував пацієнт. */
    @NotNull(message = "Палата не може бути порожньою")
    private Integer ward;

    /** Лікар, що відповідав за пацієнта. */
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private PersonalModel doctor;

    /** Додаткові нотатки щодо лікування. Максимум 500 символів. */
    @Size(max = 500, message = "Нотатки не можуть перевищувати 500 символів")
    private String notes;

    /** Дата прийому пацієнта лікарем. */
    @NotNull(message = "Дата прийому не може бути порожньою")
    private LocalDateTime appointmentDateFrom;

    /** Дата виписки пацієнта лікарем. */
    @NotNull(message = "Дата виписки не може бути порожньою")
    private LocalDateTime appointmentDateTo;

    /** Назва департаменту, до якого належав пацієнт. */
    @NotNull(message = "Департамент не додано")
    private String department;

    /** Номер ліжка, закріпленого за пацієнтом. */
    @NotNull(message = "Поле 'Ліжко' не може бути порожньою")
    private Integer bed;

    /**
     * Конструктор за замовчуванням.
     */
    public HistoryPatientsModel() {
    }

    /**
     * Повний конструктор для створення екземпляра історії пацієнта.
     *
     * @param fullName повне ім'я пацієнта
     * @param phone номер телефону
     * @param diagnosis діагноз
     * @param birthDate дата народження
     * @param ward палата
     * @param doctor лікар
     * @param notes додаткові нотатки
     * @param department департамент
     * @param appointmentDateFrom дата прийому
     * @param appointmentDateTo дата виписки
     * @param bed ліжко
     */
    public HistoryPatientsModel(String fullName, String phone, String diagnosis, LocalDate birthDate, Integer ward,
                                PersonalModel doctor, String notes, String department,
                                LocalDateTime appointmentDateFrom, LocalDateTime appointmentDateTo, Integer bed) {
        this.fullName = fullName;
        this.phone = phone;
        this.diagnosis = diagnosis;
        this.birthDate = birthDate;
        this.ward = ward;
        this.doctor = doctor;
        this.notes = notes;
        this.appointmentDateFrom = appointmentDateFrom;
        this.appointmentDateTo = appointmentDateTo;
        this.department = department;
        this.bed = bed;
    }

    // Гетери та сетери з JavaDoc

    /**
     * @return ідентифікатор пацієнта
     */
    public Long getId() { return id; }

    /**
     * @param id ідентифікатор пацієнта
     */
    public void setId(Long id) { this.id = id; }

    /**
     * @return повне ім'я пацієнта
     */
    public String getFullName() { return fullName; }

    /**
     * @param fullName повне ім'я пацієнта
     */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /**
     * @return номер телефону
     */
    public String getPhone() { return phone; }

    /**
     * @param phone номер телефону
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * @return діагноз
     */
    public String getDiagnosis() { return diagnosis; }

    /**
     * @param diagnosis діагноз
     */
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    /**
     * @return дата народження
     */
    public LocalDate getBirthDate() { return birthDate; }

    /**
     * @param birthDate дата народження
     */
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    /**
     * @return номер палати
     */
    public Integer getWard() { return ward; }

    /**
     * @param ward номер палати
     */
    public void setWard(Integer ward) { this.ward = ward; }

    /**
     * @return лікар
     */
    public PersonalModel getDoctor() { return doctor; }

    /**
     * @param doctor лікар
     */
    public void setDoctor(PersonalModel doctor) { this.doctor = doctor; }

    /**
     * @return нотатки
     */
    public String getNotes() { return notes; }

    /**
     * @param notes нотатки
     */
    public void setNotes(String notes) { this.notes = notes; }

    /**
     * @return дата прийому
     */
    public LocalDateTime getAppointmentDateFrom() { return appointmentDateFrom; }

    /**
     * @param appointmentDateFrom дата прийому
     */
    public void setAppointmentDateFrom(LocalDateTime appointmentDateFrom) { this.appointmentDateFrom = appointmentDateFrom; }

    /**
     * @return дата виписки
     */
    public LocalDateTime getAppointmentDateTo() { return appointmentDateTo; }

    /**
     * @param appointmentDateTo дата виписки
     */
    public void setAppointmentDateTo(LocalDateTime appointmentDateTo) { this.appointmentDateTo = appointmentDateTo; }

    /**
     * @return департамент
     */
    public String getDepartment() { return department; }

    /**
     * @param department департамент
     */
    public void setDepartment(String department) { this.department = department; }

    /**
     * @return ліжко
     */
    public Integer getBed() { return bed; }

    /**
     * @param bed ліжко
     */
    public void setBed(Integer bed) { this.bed = bed; }
}
