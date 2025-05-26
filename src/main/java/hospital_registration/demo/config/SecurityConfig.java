package hospital_registration.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфігураційний клас безпеки застосунку.
 * <p>
 * Визначає {@link PasswordEncoder}, який використовується для шифрування паролів
 * у Spring Security.
 * </p>
 */
@Configuration
public class SecurityConfig {

    /**
     * Створює та повертає екземпляр {@link BCryptPasswordEncoder},
     * який реалізує інтерфейс {@link PasswordEncoder}.
     *
     * @return об'єкт {@link PasswordEncoder} для хешування паролів за допомогою алгоритму BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
