package hospital_registration.demo.repo;

import hospital_registration.demo.Models.PatientModel;
import hospital_registration.demo.Models.PersonalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PatientRepo extends JpaRepository<PatientModel, Long> {

    // Пошук пацієнтів за лікарем
    List<PatientModel> findByDoctor(PersonalModel doctor);

    // Пошук пацієнтів за ID лікаря
    List<PatientModel> findByDoctor_Id(Long doctorId);

    // Пошук за ім'ям (регістронезалежний)
    @Query("SELECT p FROM PatientModel p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PatientModel> findByFullNameContainingIgnoreCase(@Param("name") String name);

    // Пошук за телефоном
    @Query("SELECT p FROM PatientModel p WHERE p.phone LIKE CONCAT('%', :phone, '%')")
    List<PatientModel> findByPhoneContaining(@Param("phone") String phone);

    // Пошук за діагнозом (регістронезалежний)
    @Query("SELECT p FROM PatientModel p WHERE LOWER(p.diagnosis) LIKE LOWER(CONCAT('%', :diagnosis, '%'))")
    List<PatientModel> findByDiagnosisContainingIgnoreCase(@Param("diagnosis") String diagnosis);

    // Пошук пацієнтів, які прийшли у певний діапазон дат
    @Query("SELECT p FROM PatientModel p WHERE p.appointmentDateTo BETWEEN :from AND :to")
    List<PatientModel> findByAppointmentDateBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT p FROM PatientModel p WHERE p.appointmentDateFrom BETWEEN :from AND :to")
    List<PatientModel> findByRecordedDate(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT p FROM PatientModel p WHERE " +
            "p.appointmentDateFrom BETWEEN :from AND :to OR " +
            "p.appointmentDateTo BETWEEN :from AND :to")
    List<PatientModel> findByDateFields(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);


    // Комбінований пошук за всіма полями
    @Query("SELECT p FROM PatientModel p WHERE " +
            "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "p.phone LIKE CONCAT('%', :searchTerm, '%') OR " +
            "LOWER(p.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<PatientModel> findByAllFieldsContaining(@Param("searchTerm") String searchTerm);

    // Пошук за лікарем та ім'ям
    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND " +
            "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PatientModel> findByDoctorIdAndFullNameContainingIgnoreCase(
            @Param("doctorId") Long doctorId, @Param("name") String name);

    // Пошук за лікарем та телефоном
    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND " +
            "p.phone LIKE CONCAT('%', :phone, '%')")
    List<PatientModel> findByDoctorIdAndPhoneContaining(
            @Param("doctorId") Long doctorId, @Param("phone") String phone);

    // Пошук за лікарем та діагнозом
    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND " +
            "LOWER(p.diagnosis) LIKE LOWER(CONCAT('%', :diagnosis, '%'))")
    List<PatientModel> findByDoctorIdAndDiagnosisContainingIgnoreCase(
            @Param("doctorId") Long doctorId, @Param("diagnosis") String diagnosis);

    // Пошук пацієнтів, які прийшли у певний діапазон дат
    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND p.appointmentDateTo BETWEEN :from AND :to")
    List<PatientModel> findByAppointmentDateBetweenDoctorId(
            @Param("doctorId") Long doctorId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND p.appointmentDateFrom BETWEEN :from AND :to")
    List<PatientModel> findByRecordedDateDoctor(@Param("doctorId") Long doctorId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND " +
            "p.appointmentDateFrom BETWEEN :from AND :to OR " +
            "p.appointmentDateTo BETWEEN :from AND :to")
    List<PatientModel> findByDateFieldsDoctor(@Param("doctorId") Long doctorId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Комбінований пошук за лікарем та всіма полями
    @Query("SELECT p FROM PatientModel p WHERE p.doctor.id = :doctorId AND (" +
            "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "p.phone LIKE CONCAT('%', :searchTerm, '%') OR " +
            "LOWER(p.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<PatientModel> findByDoctorIdAndAllFieldsContaining(
            @Param("doctorId") Long doctorId, @Param("searchTerm") String searchTerm);
}
