package hospital_registration.demo.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class PersonalModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Ім'я не може бути порожнім")
    private String fullName;

    @NotBlank(message = "Логін не може бути порожнім")
    @Column(unique = true)
    private String login;

    @NotNull(message = "Номер телефону не може бути порожнім")
    @Min(value = 100000000, message = "Некоректний номер телефону")
    private Integer phone = 0;

    @NotBlank(message = "Позиція не може бути порожньою")
    private String position;

    @NotBlank(message = "Спеціалізація не може бути порожньою")
    private String specialty;

    @NotBlank(message = "Пароль не може бути порожнім")
    private String access_key;

    public PersonalModel() {
    }

    public PersonalModel(String fullName, String login, Integer phone, String position, String specialty, String access_key) {
        this.fullName = fullName;
        this.login = login;
        this.phone = (phone != null) ? phone : 0;
        this.position = position;
        this.specialty = specialty;
        this.access_key = access_key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = (phone != null) ? phone : 0;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAccess_key() {
        return access_key;
    }

    public void setAccess_key(String access_key) {
        this.access_key = access_key;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
