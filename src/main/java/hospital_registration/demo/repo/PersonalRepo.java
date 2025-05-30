package hospital_registration.demo.repo;

import java.util.List;
import java.util.Optional;
import hospital_registration.demo.Models.PersonalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для персоналу лікарні {@link PersonalModel}.
 * Додатково містить методи пошуку за логіном та перевірки посади.
 */

@Repository
public interface PersonalRepo extends JpaRepository<PersonalModel, Long> {

    /**
     * Знаходить співробітника за логіном.
     *
     * @param login логін співробітника
     * @return Optional з моделлю співробітника або порожній Optional
     */
    Optional<PersonalModel> findByLogin(String login);

    /**
     * Знаходить співробітника за електронною поштою.
     *
     * @param email електронна пошта
     * @return Optional з моделлю співробітника або порожній Optional
     */
    Optional<PersonalModel> findByEmail(String email);

    /**
     * Знаходить всіх співробітників за посадою (ігноруючи регістр).
     *
     * @param position посада
     * @return список співробітників з указаною посадою
     */
    List<PersonalModel> findByPositionIgnoreCase(String position);

    /**
     * Знаходить всіх співробітників за спеціалізацією (ігноруючи регістр).
     *
     * @param specialty спеціалізація
     * @return список співробітників з указаною спеціалізацією
     */
    List<PersonalModel> findBySpecialtyIgnoreCase(String specialty);

    /**
     * Знаходить співробітників, ім'я яких містить задану підстроку (ігноруючи регістр).
     *
     * @param fullName частина повного імені
     * @return список співробітників, імена яких містять підстроку
     */
    List<PersonalModel> findByFullNameContainingIgnoreCase(String fullName);

    /**
     * Знаходить співробітників, логін яких містить задану підстроку (ігноруючи регістр).
     *
     * @param login частина логіну
     * @return список співробітників, логіни яких містять підстроку
     */
    List<PersonalModel> findByLoginContainingIgnoreCase(String login);

    /**
     * Знаходить співробітників, email яких містить задану підстроку (ігноруючи регістр).
     *
     * @param email частина електронної пошти
     * @return список співробітників, email яких містить підстроку
     */
    List<PersonalModel> findByEmailContainingIgnoreCase(String email);

    /**
     * Перевіряє, чи існує співробітник з таким логіном.
     *
     * @param login логін для перевірки
     * @return true, якщо співробітник існує
     */
    boolean existsByLogin(String login);

    /**
     * Перевіряє, чи існує співробітник з таким email.
     *
     * @param email email для перевірки
     * @return true, якщо співробітник існує
     */
    boolean existsByEmail(String email);

    /**
     * Перевіряє, чи існує персонал із вказаною посадою.
     *
     * @param position назва посади
     * @return true, якщо існує, інакше false
     */
    boolean existsByPosition(String position);

    /**
     * Знаходить співробітників за телефоном.
     *
     * @param phone номер телефону
     * @return список співробітників з указаним телефоном
     */
    Optional<PersonalModel> findByPhone(String phone);

    /**
     * Універсальний пошук по всіх текстових полях.
     * Шукає співробітників, у яких хоча б одне поле містить задану підстроку.
     *
     * ВИПРАВЛЕНО: Використовується STR() замість CAST для конвертації Integer в String
     *
     * @param searchTerm термін для пошуку
     * @return список співробітників, що відповідають критерію пошуку
     */
    @Query("SELECT p FROM PersonalModel p WHERE " +
            "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.login) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.position) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.specialty) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "STR(p.phone) LIKE CONCAT('%', :searchTerm, '%')")
    List<PersonalModel> findByAllFieldsContaining(@Param("searchTerm") String searchTerm);

    /**
     * Знаходить всіх лікарів (включаючи головного лікаря).
     *
     * @return список всіх лікарів
     */
    @Query("SELECT p FROM PersonalModel p WHERE LOWER(p.position) LIKE '%лікар%'")
    List<PersonalModel> findAllDoctors();

    /**
     * Знаходить всіх медсестер.
     *
     * @return список всіх медсестер
     */
    @Query("SELECT p FROM PersonalModel p WHERE LOWER(p.position) LIKE '%сестр%'")
    List<PersonalModel> findAllNurses();

    /**
     * Підраховує кількість співробітників за посадою.
     *
     * @param position посада
     * @return кількість співробітників
     */
    long countByPositionIgnoreCase(String position);

    @Query("SELECT p FROM PersonalModel p WHERE LOWER(p.position) <> 'головний лікар'")
    List<PersonalModel> findAllExceptHeadDoctor();

}