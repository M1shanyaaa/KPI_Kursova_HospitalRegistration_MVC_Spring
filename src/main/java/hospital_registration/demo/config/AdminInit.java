package hospital_registration.demo.config;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.PersonalRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminInit {

    @Bean
    public CommandLineRunner createAdminIfNotExists(PersonalRepo personalRepo) {
        return args -> {
            String adminLogin = "admin"; // Логін адміністратора
            String adminPassword = "admin123"; // Початковий пароль (потрібно змінити вручну після першого входу)

            if (!personalRepo.existsByPosition("Головний лікар")){
                PersonalModel admin = new PersonalModel("Рибак Михайло", adminLogin, 300000051, "Головний лікар", "Хірург", adminPassword);
                personalRepo.save(admin);
                System.out.println("Адміністратор створений: Логін - " + adminLogin);
            }
        };
    }
}
