package hospital_registration.demo.config;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.PersonalRepo;
import hospital_registration.demo.service.PasswordService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminInit {

    @Bean
    public CommandLineRunner createAdminIfNotExists(PersonalRepo personalRepo, PasswordService passwordService) {
        return args -> {
            String adminLogin = "admin";
            String adminPassword = "admin";

            if (!personalRepo.existsByPosition("Головний лікар")) {
                String hashedPassword = passwordService.encodePassword(adminPassword);
                PersonalModel admin = new PersonalModel(
                        "Рибак Михайло",
                        adminLogin,
                        960741514,
                        "Головний лікар",
                        "Хірург",
                        hashedPassword,
                        "tokariuk.stanislav@lll.kpi.ua"
                );
                personalRepo.save(admin);
                System.out.println("Адміністратор створений: Логін - " + adminLogin);
            }
        };
    }
}
