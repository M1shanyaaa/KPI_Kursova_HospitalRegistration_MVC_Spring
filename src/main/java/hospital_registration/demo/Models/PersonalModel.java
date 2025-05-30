package hospital_registration.demo.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Представляє персонал лікарні (наприклад, лікаря чи медичну сестру).
 * Містить особисту інформацію, логін, спеціалізацію та контактні дані.
 */
@Entity
public class PersonalModel {

    /** Унікальний ідентифікатор персоналу */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** Повне ім’я співробітника. Обов’язкове поле. */
    @NotBlank(message = "Ім'я не може бути порожнім")
    @Pattern(regexp = ".*[a-zA-Zа-яА-ЯіІїЇєЄґҐ].*", message = "Ім'я має містити хоча б одну букву")
    private String fullName;

    /** Унікальний логін для входу в систему. */
    @NotBlank(message = "Логін не може бути порожнім")
    @Column(unique = true)
    private String login;

    /** Номер телефону. Має бути щонайменше 9-значним числом. */
    @NotNull(message = "Номер телефону не може бути порожнім")
    @Pattern(regexp = "0\\d{9}", message = "Телефон має бути у форматі 0xxxxxxxxx")
    private String phone;

    /** Посада співробітника (наприклад, лікар, медсестра). */
    @NotBlank(message = "Позиція не може бути порожньою")
    private String position;

    /** Спеціалізація (наприклад, кардіолог, хірург). */
    @NotBlank(message = "Спеціалізація не може бути порожньою")
    private String specialty;

    /** Пароль або ключ доступу до системи. */
    @NotBlank(message = "Пароль не може бути порожнім")
    private String access_key;

    /** Email персоналу. Має бути унікальним та відповідати email-формату. */
    @NotBlank(message = "Email не може бути порожнім")
    @Email(message = "Некоректний формат email")
    @Column(unique = true)
    private String email;

    /**
     * Конструктор за замовчуванням.
     */
    public PersonalModel() {}

    /**
     * Конструктор з усіма полями.
     *
     * @param fullName повне ім’я
     * @param login логін
     * @param phone номер телефону
     * @param position посада
     * @param specialty спеціалізація
     * @param access_key ключ доступу
     * @param email електронна пошта
     */
    public PersonalModel(String fullName, String login, String phone, String position,
                         String specialty, String access_key, String email) {
        this.fullName = fullName;
        this.login = login;
        this.phone = phone;
        this.position = position;
        this.specialty = specialty;
        this.access_key = access_key;
        this.email = email;
    }

    /** @return ID персоналу */
    public Long getId() {
        return id;
    }

    /** @param id ID персоналу */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return повне ім’я */
    public String getFullName() {
        return fullName;
    }

    /** @param fullName повне ім’я */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /** @return логін */
    public String getLogin() {
        return login;
    }

    /** @param login логін */
    public void setLogin(String login) {
        this.login = login;
    }

    /** @return номер телефону */
    public String getPhone() {
        return phone;
    }

    /** @param phone номер телефону */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** @return посада */
    public String getPosition() {
        return position;
    }

    /** @param position посада */
    public void setPosition(String position) {
        this.position = position;
    }

    /** @return ключ доступу */
    public String getAccess_key() {
        return access_key;
    }

    /** @param access_key ключ доступу */
    public void setAccess_key(String access_key) {
        this.access_key = access_key;
    }

    /** @return спеціалізація */
    public String getSpecialty() {
        return specialty;
    }

    /** @param specialty спеціалізація */
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    /** @return електронна пошта */
    public String getEmail() {
        return email;
    }

    /** @param email електронна пошта */
    public void setEmail(String email) {
        this.email = email;
    }
}
