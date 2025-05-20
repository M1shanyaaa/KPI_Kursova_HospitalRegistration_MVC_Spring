package hospital_registration.demo.repo;

import java.util.Optional;
import hospital_registration.demo.Models.PersonalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для персоналу лікарні {@link PersonalModel}.
 * Додатково містить методи пошуку за логіном та перевірки посади.
 */
@Repository
public interface PersonalRepo extends JpaRepository<PersonalModel, Long> {

    /**
     * Пошук персоналу за логіном.
     *
     * @param login логін користувача
     * @return {@link Optional} персоналу
     */
    Optional<PersonalModel> findByLogin(String login);

    /**
     * Перевірка, чи існує персонал із вказаною посадою.
     *
     * @param position назва посади
     * @return true, якщо існує, інакше false
     */
    boolean existsByPosition(String position);
}
