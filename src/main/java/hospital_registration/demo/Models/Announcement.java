package hospital_registration.demo.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * Клас представляє сутність оголошення в системі реєстрації лікарні.
 * Містить назву, вміст та дату створення оголошення.
 */
@Entity
public class Announcement {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Назва оголошення. Не може бути порожньою.
     */
    @NotNull(message = "Назва оголошення не може бути порожньою")
    private String title;

    /**
     * Вміст оголошення. Не може бути порожнім.
     */
    @NotNull(message = "Вміст оголошення не може бути порожньою")
    private String content;

    /**
     * Дата та час створення оголошення. Встановлюється автоматично перед збереженням.
     */
    private LocalDateTime createdAt;

    /**
     * Метод, що автоматично викликається перед збереженням оголошення.
     * Встановлює поточну дату та час як createdAt.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Конструктор без параметрів.
     */
    public Announcement() {
    }

    /**
     * Повертає ідентифікатор оголошення.
     *
     * @return ID оголошення
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор оголошення.
     *
     * @param id новий ID оголошення
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Повертає назву оголошення.
     *
     * @return назва оголошення
     */
    public @NotNull(message = "Назва оголошення не може бути порожньою") String getTitle() {
        return title;
    }

    /**
     * Встановлює назву оголошення.
     *
     * @param title назва оголошення
     */
    public void setTitle(@NotNull(message = "Назва оголошення не може бути порожньою") String title) {
        this.title = title;
    }

    /**
     * Повертає вміст оголошення.
     *
     * @return вміст оголошення
     */
    public @NotNull(message = "Вміст оголошення не може бути порожньою") String getContent() {
        return content;
    }

    /**
     * Встановлює вміст оголошення.
     *
     * @param content вміст оголошення
     */
    public void setContent(@NotNull(message = "Вміст оголошення не може бути порожньою") String content) {
        this.content = content;
    }

    /**
     * Повертає дату та час створення оголошення.
     *
     * @return дата створення
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Встановлює дату та час створення оголошення.
     *
     * @param createdAt дата створення
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
