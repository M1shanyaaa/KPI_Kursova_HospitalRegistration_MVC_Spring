package hospital_registration.demo.repo;

import hospital_registration.demo.Models.PatientModel;
import hospital_registration.demo.Models.PersonalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторій для активних пацієнтів {@link PatientModel}.
 * Додатково містить методи пошуку пацієнтів за лікарем.
 */
@Repository
public interface PatientRepo extends JpaRepository<PatientModel, Long> {

    /**
     * Знаходить усіх пацієнтів, прикріплених до певного лікаря.
     *
     * @param doctor екземпляр {@link PersonalModel}
     * @return список пацієнтів
     */
    List<PatientModel> findByDoctor(PersonalModel doctor);

    /**
     * Знаходить пацієнтів за ID лікаря.
     *
     * @param doctorId ID лікаря
     * @return список пацієнтів
     */
    List<PatientModel> findByDoctor_Id(Long doctorId);
}
