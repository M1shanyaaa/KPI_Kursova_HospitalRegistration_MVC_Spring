package hospital_registration.demo.repo;

import hospital_registration.demo.Models.PatientModel;
import hospital_registration.demo.Models.PersonalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторій для роботи з даними пацієнтів.
 * Реалізує доступ до бази даних для сутності {@link PatientModel}.
 * Забезпечує пошук пацієнтів за різними критеріями, включаючи ім'я, телефон, діагноз,
 * діапазони дат та лікарів.
 */
@Repository
public interface PatientRepo extends JpaRepository<PatientModel, Long> {

    /**
     * Пошук пацієнтів за лікарем.
     *
     * @param doctor об'єкт лікаря
     * @return список пацієнтів, що закріплені за даним лікарем
     */
    List<PatientModel> findByDoctor(PersonalModel doctor);

    /**
     * Пошук пацієнтів за ID лікаря.
     *
     * @param doctorId ідентифікатор лікаря
     * @return список пацієнтів, що закріплені за даним лікарем
     */
    List<PatientModel> findByDoctor_Id(Long doctorId);

    /**
     * Пошук пацієнтів за ім'ям (незалежно від регістру).
     *
     * @param name часткове або повне ім'я пацієнта
     * @return список знайдених пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PatientModel> findByFullNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Пошук пацієнтів за номером телефону.
     *
     * @param phone частковий або повний номер телефону пацієнта
     * @return список знайдених пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE p.phone LIKE CONCAT('%', :phone, '%')")
    List<PatientModel> findByPhoneContaining(@Param("phone") String phone);

    /**
     * Пошук пацієнтів за діагнозом (незалежно від регістру).
     *
     * @param diagnosis діагноз або його частина
     * @return список знайдених пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE LOWER(p.diagnosis) LIKE LOWER(CONCAT('%', :diagnosis, '%'))")
    List<PatientModel> findByDiagnosisContainingIgnoreCase(@Param("diagnosis") String diagnosis);

    /**
     * Пошук пацієнтів за діапазоном дати прийому (дата виходу).
     *
     * @param from початкова дата
     * @param to кінцева дата
     * @return список пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE p.appointmentDateTo BETWEEN :from AND :to")
    List<PatientModel> findByAppointmentDateBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Пошук пацієнтів за діапазоном дати запису.
     *
     * @param from початкова дата
     * @param to кінцева дата
     * @return список пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE p.appointmentDateFrom BETWEEN :from AND :to")
    List<PatientModel> findByRecordedDate(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Пошук пацієнтів за діапазоном дати запису або дати прийому.
     *
     * @param from початкова дата
     * @param to кінцева дата
     * @return список пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE " +
            "p.appointmentDateFrom BETWEEN :from AND :to OR " +
            "p.appointmentDateTo BETWEEN :from AND :to")
    List<PatientModel> findByDateFields(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Пошук пацієнтів за всіма полями: ім'я, телефон, діагноз.
     *
     * @param searchTerm ключове слово для пошуку
     * @return список пацієнтів, що відповідають критеріям
     */
    @Query("SELECT p FROM PatientModel p WHERE " +
            "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "p.phone LIKE CONCAT('%', :searchTerm, '%') OR " +
            "LOWER(p.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<PatientModel> findByAllFieldsContaining(@Param("searchTerm") String searchTerm);

    /**
     * Пошук пацієнтів за лікарем та ім'ям.
     *
     * @param doctorId ID лікаря
     * @param name     ім'я або його частина
     * @return список знайдених пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND " +
            "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PatientModel> findByDoctorIdAndFullNameContainingIgnoreCase(
            @Param("doctorId") Long doctorId, @Param("name") String name);

    /**
     * Пошук пацієнтів за лікарем та телефоном.
     *
     * @param doctorId ID лікаря
     * @param phone    телефон або його частина
     * @return список знайдених пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND " +
            "p.phone LIKE CONCAT('%', :phone, '%')")
    List<PatientModel> findByDoctorIdAndPhoneContaining(
            @Param("doctorId") Long doctorId, @Param("phone") String phone);

    /**
     * Пошук пацієнтів за лікарем та діагнозом.
     *
     * @param doctorId ID лікаря
     * @param diagnosis діагноз або його частина
     * @return список знайдених пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND " +
            "LOWER(p.diagnosis) LIKE LOWER(CONCAT('%', :diagnosis, '%'))")
    List<PatientModel> findByDoctorIdAndDiagnosisContainingIgnoreCase(
            @Param("doctorId") Long doctorId, @Param("diagnosis") String diagnosis);

    /**
     * Пошук пацієнтів за лікарем та діапазоном дати прийому.
     *
     * @param doctorId ID лікаря
     * @param from     початкова дата
     * @param to       кінцева дата
     * @return список знайдених пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND p.appointmentDateTo BETWEEN :from AND :to")
    List<PatientModel> findByAppointmentDateBetweenDoctorId(
            @Param("doctorId") Long doctorId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Пошук пацієнтів за лікарем та діапазоном дати запису.
     *
     * @param doctorId ID лікаря
     * @param from     початкова дата
     * @param to       кінцева дата
     * @return список знайдених пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND p.appointmentDateFrom BETWEEN :from AND :to")
    List<PatientModel> findByRecordedDateDoctor(@Param("doctorId") Long doctorId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Пошук пацієнтів за лікарем та діапазоном дати запису або дати прийому.
     *
     * @param doctorId ID лікаря
     * @param from     початкова дата
     * @param to       кінцева дата
     * @return список знайдених пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND " +
            "p.appointmentDateFrom BETWEEN :from AND :to OR " +
            "p.appointmentDateTo BETWEEN :from AND :to")
    List<PatientModel> findByDateFieldsDoctor(@Param("doctorId") Long doctorId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Комбінований пошук пацієнтів за лікарем та всіма полями: ім'я, телефон, діагноз.
     *
     * @param doctorId   ID лікаря
     * @param searchTerm ключове слово для пошуку
     * @return список знайдених пацієнтів
     */
    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND (" +
            "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "p.phone LIKE CONCAT('%', :searchTerm, '%') OR " +
            "LOWER(p.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<PatientModel> findByDoctorIdAndAllFieldsContaining(
            @Param("doctorId") Long doctorId, @Param("searchTerm") String searchTerm);
}
