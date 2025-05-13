//package hospital_registration.demo.service;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class PasswordService {
//    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//    /**
//     * Шифрує пароль за допомогою BCrypt
//     * @param rawPassword необроблений пароль
//     * @return зашифрований пароль
//     */
//    public String encodePassword(String rawPassword) {
//        return passwordEncoder.encode(rawPassword);
//    }
//
//    /**
//     * Перевіряє, чи збігається введений пароль із зашифрованим
//     * @param rawPassword введений пароль
//     * @param encodedPassword зашифрований пароль
//     * @return true, якщо паролі збігаються
//     */
//    public boolean matchesPassword(String rawPassword, String encodedPassword) {
//        return passwordEncoder.matches(rawPassword, encodedPassword);
//    }
//}
//
