package hospital_registration.demo.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Сервіс для надсилання email-повідомлень, що імітують SMS-повідомлення (наприклад, через шлюз email-to-SMS).
 * Може використовуватись для сповіщення пацієнтів або персоналу.
 */
@Service
public class SmsService {

    /**
     * Компонент для відправки email-повідомлень.
     */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Надсилає email на вказану адресу з темою та HTML-текстом.
     *
     * @param toEmail  Адреса одержувача
     * @param subject  Тема повідомлення
     * @param body     Основний вміст повідомлення у вигляді HTML
     * @throws Exception якщо виникає помилка при формуванні або надсиланні повідомлення
     */
    public void sendEmail(String toEmail, String subject, String body) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("Реєстратура Лікарні <hospitalregistrat1on69@gmail.com>");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText("<h3>" + subject + "</h3><p>" + body + "</p>", true);

        mailSender.send(message);
    }
}
