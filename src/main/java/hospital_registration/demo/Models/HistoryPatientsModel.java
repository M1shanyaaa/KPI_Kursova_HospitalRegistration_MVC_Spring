package hospital_registration.demo.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "history_patients")
public class HistoryPatientsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ім'я не може бути порожнім")
    private String fullName;

    @NotBlank(message = "Номер телефону не може бути порожнім")
    @Pattern(regexp = "\\+?\\d{10,15}", message = "Некоректний формат телефону")
    private String phone;

    @NotBlank(message = "Діагноз не може бути порожнім")
    @Size(max = 255, message = "Діагноз не може перевищувати 255 символів")
    private String diagnosis;

    @NotNull(message = "Дата народження не може бути порожньою")
    private LocalDate birthDate;

    @NotNull(message = "Палата не може бути порожньою")
    private Integer ward;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private PersonalModel doctor;

    @Size(max = 500, message = "Нотатки не можуть перевищувати 500 символів")
    private String notes;

    @NotNull(message = "Дата прийому не може бути порожньою")
    private LocalDateTime appointmentDateFrom; // Дата запису пацієнта лікарем

    @NotNull(message = "Дата виписки не може бути порожньою")
    private LocalDateTime appointmentDateTo; // Дата виписування пацієнта лікарем

    @NotNull(message = "Департамент не додано")
    private String department; //

    @NotNull(message = "Поле 'Ліжко' не може бути порожньою")
    private Integer bed;

    public HistoryPatientsModel() {
    }

    public HistoryPatientsModel(String fullName, String phone, String diagnosis, LocalDate birthDate, Integer ward, PersonalModel doctor, String notes, String department , LocalDateTime appointmentDateFrom,LocalDateTime appointmentDateTo, Integer bed) {
        this.fullName = fullName;
        this.phone = phone;
        this.diagnosis = diagnosis;
        this.birthDate = birthDate;
        this.ward = ward;
        this.doctor = doctor;
        this.notes = notes;
        this.appointmentDateFrom = appointmentDateFrom;
        this.appointmentDateTo =appointmentDateTo;
        this.department = department;
        this.bed = bed;
    }

    // Гетери та сетери
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Integer getWard() { return ward; }
    public void setWard(Integer ward) { this.ward = ward; }

    public PersonalModel getDoctor() { return doctor; }
    public void setDoctor(PersonalModel doctor) { this.doctor = doctor; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getAppointmentDateFrom() { return appointmentDateFrom; }
    public void setAppointmentDateFrom(LocalDateTime appointmentDateFrom) { this.appointmentDateFrom = appointmentDateFrom; }

    public LocalDateTime getAppointmentDateTo() { return appointmentDateTo; }
    public void setAppointmentDateTo(LocalDateTime appointmentDateTo) { this.appointmentDateTo = appointmentDateTo; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getBed() { return bed; }
    public void setBed(Integer bed) { this.bed = bed; }
}