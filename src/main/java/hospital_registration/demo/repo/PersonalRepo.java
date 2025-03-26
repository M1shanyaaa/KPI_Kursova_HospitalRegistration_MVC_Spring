package hospital_registration.demo.repo;

import java.util.Optional;
import hospital_registration.demo.Models.PersonalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalRepo extends JpaRepository<PersonalModel, Long> {
    Optional<PersonalModel> findByLogin(String login);  // Залишаємо метод для пошуку за логіном
    boolean existsByPosition(String position);
}
