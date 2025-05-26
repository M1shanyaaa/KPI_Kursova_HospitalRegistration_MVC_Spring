package hospital_registration.demo.repo;

import hospital_registration.demo.Models.HistoryPatientsModel;
import hospital_registration.demo.Models.PersonalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторій для роботи з історією пацієнтів {@link HistoryPatientsModel}.
 * Забезпечує пошук пацієнтів за різними критеріями:
 * лікар, ім'я, телефон, діагноз, дати виписки та запису тощо.
 */
@Repository
public interface HistoryPatientRepo extends JpaRepository<HistoryPatientsModel, Long> {

    /**
     * Знаходить усіх історичних пацієнтів, які були закріплені за конкретним лікарем.
     * @param doctor лікар (PersonalModel)
     * @return список пацієнтів
     */
    List<HistoryPatientsModel> findByDoctor(PersonalModel doctor);

    /**
     * Знаходить усіх історичних пацієнтів за ID лікаря.
     * @param doctorId ID лікаря
     * @return список пацієнтів
     */
    List<HistoryPatientsModel> findByDoctor_Id(Long doctorId);

    /**
     * Пошук пацієнтів за частковим збігом імені (регістронезалежно).
     * @param name частина імені для пошуку
     * @return список пацієнтів
     */
    @Query("SELECT h FROM HistoryPatientsModel h WHERE LOWER(h.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<HistoryPatientsModel> findByFullNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Пошук пацієнтів за частковим збігом телефону.
     * @param phone частина телефону для пошуку
     * @return список пацієнтів
     */
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.phone LIKE CONCAT('%', :phone, '%')")
    List<HistoryPatientsModel> findByPhoneContaining(@Param("phone") String phone);

    /**
     * Пошук пацієнтів за частковим збігом діагнозу (регістронезалежно).
     * @param diagnosis частина діагнозу для пошуку
     * @return список пацієнтів
     */
    @Query("SELECT h FROM HistoryPatientsModel h WHERE LOWER(h.diagnosis) LIKE LOWER(CONCAT('%', :diagnosis, '%'))")
    List<HistoryPatientsModel> findByDiagnosisContainingIgnoreCase(@Param("diagnosis") String diagnosis);

    /**
     * Пошук пацієнтів, які були виписані у певному діапазоні дат.
     * Перевіряється поле appointmentDateTo.
     * @param from початок діапазону
     * @param to кінець діапазону
     * @return список пацієнтів
     */
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.appointmentDateTo BETWEEN :from AND :to")
    List<HistoryPatientsModel> findByDischargeDate(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Пошук пацієнтів за датою запису (appointmentDateFrom) у заданому діапазоні.
     * @param from початок діапазону
     * @param to кінець діапазону
     * @return список пацієнтів
     */
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.appointmentDateFrom BETWEEN :from AND :to")
    List<HistoryPatientsModel> findByRecordedDate(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Пошук пацієнтів, у яких appointmentDateFrom або appointmentDateTo потрапляє у заданий діапазон.
     * @param from початок діапазону
     * @param to кінець діапазону
     * @return список пацієнтів
     */
    @Query("SELECT h FROM HistoryPatientsModel h WHERE " +
            "h.appointmentDateFrom BETWEEN :from AND :to OR " +
            "h.appointmentDateTo BETWEEN :from AND :to")
    List<HistoryPatientsModel> findByDateFields(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Комбінований пошук пацієнтів за іменем, телефоном або діагнозом (регістронезалежно).
     * @param searchTerm текст для пошуку
     * @return список пацієнтів
     */
    @Query("SELECT h FROM HistoryPatientsModel h WHERE " +
            "LOWER(h.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "h.phone LIKE CONCAT('%', :searchTerm, '%') OR " +
            "LOWER(h.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<HistoryPatientsModel> findByAllFieldsContaining(@Param("searchTerm") String searchTerm);

    /**
     * Пошук пацієнтів за ID лікаря та частковим збігом імені (регістронезалежно).
     * @param doctorId ID лікаря
     * @param name частина імені
     * @return список пацієнтів
     */
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.doctor.id = :doctorId AND " +
            "LOWER(h.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<HistoryPatientsModel> findByDoctorIdAndFullNameContainingIgnoreCase(
            @Param("doctorId") Long doctorId, @Param("name") String name);

    /**
     * Пошук пацієнтів за ID лікаря та частковим збігом телефону.
     * @param doctorId ID лікаря
     * @param phone частина телефону
     * @return список пацієнтів
     */
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.doctor.id = :doctorId AND " +
            "h.phone LIKE CONCAT('%', :phone, '%')")
    List<HistoryPatientsModel> findByDoctorIdAndPhoneContaining(
            @Param("doctorId") Long doctorId, @Param("phone") String phone);

    /**
     * Пошук пацієнтів за ID лікаря та частковим збігом діагнозу (регістронезалежно).
     * @param doctorId ID лікаря
     * @param diagnosis частина діагнозу
     * @return список пацієнтів
     */
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.doctor.id = :doctorId AND " +
            "LOWER(h.diagnosis) LIKE LOWER(CONCAT('%', :diagnosis, '%'))")
    List<HistoryPatientsModel> findByDoctorIdAndDiagnosisContainingIgnoreCase(
            @Param("doctorId") Long doctorId, @Param("diagnosis") String diagnosis);

    /**
     * Комбінований пошук пацієнтів за ID лікаря та збігом по імені, телефону або діагнозу.
     * @param doctorId ID лікаря
     * @param searchTerm текст для пошуку
     * @return список пацієнтів
     */
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.doctor.id = :doctorId AND (" +
            "LOWER(h.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "h.phone LIKE CONCAT('%', :searchTerm, '%') OR " +
            "LOWER(h.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<HistoryPatientsModel> findByDoctorIdAndAllFieldsContaining(
            @Param("doctorId") Long doctorId, @Param("searchTerm") String searchTerm);
}
