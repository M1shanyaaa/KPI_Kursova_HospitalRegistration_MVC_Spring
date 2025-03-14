package hospital_registration.demo.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
public class NurseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Ім'я не може бути порожнім")
    private String full_name;

    @NotBlank(message = "Логін не може бути порожнім")
    private String login;

    @NotNull(message = "Номер телефону не може бути порожнім")
    @Min(value = 100000000, message = "Некоректний номер телефону")
    private Integer phone = 0; // Значення за замовчуванням, щоб уникнути null

    @NotBlank(message = "Позиція не може бути порожньою")
    private String position;

    @NotBlank(message = "Пароль не може бути порожнім")
    private String access_key;

    // Конструктор без параметрів
    public NurseModel() {
    }

    // Конструктор із параметрами
    public NurseModel(String full_name, String login, Integer phone, String position, String access_key) {
        this.full_name = full_name;
        this.login = login;
        this.phone = (phone != null) ? phone : 0; // Захист від null
        this.position = position;
        this.access_key = access_key;
    }

    // Гетери та сетери
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getPhone() {
        return (phone != null) ? phone : 0; // Захист від null
    }

    public void setPhone(Integer phone) {
        this.phone = (phone != null) ? phone : 0; // Захист від null
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

}
