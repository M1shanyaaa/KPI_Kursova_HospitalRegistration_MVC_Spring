package hospital_registration.demo.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class SmsService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("Реєстратура Лікарні <hospitalregistrat1on69@gmail.com>");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText("<h3>" + subject + "</h3><p>" + body + "</p>", true); // HTML

        mailSender.send(message);
    }
}
