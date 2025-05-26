package hospital_registration.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервіс для кодування та перевірки паролів за допомогою {@link PasswordEncoder}.
 * <p>
 * Цей сервіс інкапсулює логіку хешування паролів і порівняння
 * сирих паролів із хешованими значеннями.
 * </p>
 */
@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор, який інжектить {@link PasswordEncoder}.
     *
     * @param passwordEncoder компонент для хешування паролів
     */
    @Autowired
    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Хешує (шифрує) заданий сирий пароль.
     *
     * @param rawPassword сирий (незашифрований) пароль
     * @return хешований (зашифрований) пароль
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Перевіряє, чи відповідає сирий пароль хешованому паролю.
     *
     * @param rawPassword     сирий пароль, введений користувачем
     * @param encodedPassword збережений хешований пароль
     * @return {@code true}, якщо паролі збігаються; інакше {@code false}
     */
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
