package hospital_registration.demo.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
public class Announcement {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Назва оголошення не може бути порожньою")
    private String title;
    @NotNull(message = "Вміст оголошення не може бути порожньою")
    private String content;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Announcement() {
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public @NotNull(message = "Назва оголошення не може бути порожньою") String getTitle() {
        return title;
    }

    public void setTitle(@NotNull(message = "Назва оголошення не може бути порожньою") String title) {
        this.title = title;
    }

    public @NotNull(message = "Вміст оголошення не може бути порожньою") String getContent() {
        return content;
    }

    public void setContent(@NotNull(message = "Вміст оголошення не може бути порожньою") String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
