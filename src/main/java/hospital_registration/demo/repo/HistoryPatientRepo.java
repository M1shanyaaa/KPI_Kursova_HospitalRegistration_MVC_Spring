package hospital_registration.demo.repo;

import hospital_registration.demo.Models.HistoryPatientsModel;
import hospital_registration.demo.Models.PersonalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface HistoryPatientRepo extends JpaRepository<HistoryPatientsModel, Long> {
    // Пошук пацієнтів за лікарем
    List<HistoryPatientsModel> findByDoctor(PersonalModel doctor);

    // Пошук пацієнтів за ID лікаря
    List<HistoryPatientsModel> findByDoctor_Id(Long doctorId);

    // Пошук за ім'ям (регістронезалежний)
    @Query("SELECT h FROM HistoryPatientsModel h WHERE LOWER(h.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<HistoryPatientsModel> findByFullNameContainingIgnoreCase(@Param("name") String name);

    // Пошук за телефоном
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.phone LIKE CONCAT('%', :phone, '%')")
    List<HistoryPatientsModel> findByPhoneContaining(@Param("phone") String phone);

    // Пошук за діагнозом (регістронезалежний)
    @Query("SELECT h FROM HistoryPatientsModel h WHERE LOWER(h.diagnosis) LIKE LOWER(CONCAT('%', :diagnosis, '%'))")
    List<HistoryPatientsModel> findByDiagnosisContainingIgnoreCase(@Param("diagnosis") String diagnosis);

    // Пошук пацієнтів виписаних у певну дату
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.appointmentDateTo BETWEEN :from AND :to")
    List<HistoryPatientsModel> findByDischargeDate(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Комбінований пошук за всіма полями
    @Query("SELECT h FROM HistoryPatientsModel h WHERE " +
            "LOWER(h.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "h.phone LIKE CONCAT('%', :searchTerm, '%') OR " +
            "LOWER(h.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(h.appointmentDateTo AS string) LIKE CONCAT('%', :searchTerm, '%')")
    List<HistoryPatientsModel> findByAllFieldsContaining(@Param("searchTerm") String searchTerm);

    // Пошук за лікарем та ім'ям
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.doctor.id = :doctorId AND " +
            "LOWER(h.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<HistoryPatientsModel> findByDoctorIdAndFullNameContainingIgnoreCase(
            @Param("doctorId") Long doctorId, @Param("name") String name);

    // Пошук за лікарем та телефоном
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.doctor.id = :doctorId AND " +
            "h.phone LIKE CONCAT('%', :phone, '%')")
    List<HistoryPatientsModel> findByDoctorIdAndPhoneContaining(
            @Param("doctorId") Long doctorId, @Param("phone") String phone);

    // Пошук за лікарем та діагнозом
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.doctor.id = :doctorId AND " +
            "LOWER(h.diagnosis) LIKE LOWER(CONCAT('%', :diagnosis, '%'))")
    List<HistoryPatientsModel> findByDoctorIdAndDiagnosisContainingIgnoreCase(
            @Param("doctorId") Long doctorId, @Param("diagnosis") String diagnosis);

    // Комбінований пошук за лікарем та всіма полями
    @Query("SELECT h FROM HistoryPatientsModel h WHERE h.doctor.id = :doctorId AND (" +
            "LOWER(h.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "h.phone LIKE CONCAT('%', :searchTerm, '%') OR " +
            "LOWER(h.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<HistoryPatientsModel> findByDoctorIdAndAllFieldsContaining(
            @Param("doctorId") Long doctorId, @Param("searchTerm") String searchTerm);

}