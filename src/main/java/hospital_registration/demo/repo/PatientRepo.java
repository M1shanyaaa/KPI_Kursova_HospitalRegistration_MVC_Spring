package hospital_registration.demo.repo;

import hospital_registration.demo.Models.PatientModel;
import hospital_registration.demo.Models.PersonalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepo extends JpaRepository<PatientModel, Long> {
    // Пошук пацієнтів за лікарем
    List<PatientModel> findByDoctor(PersonalModel doctor);

    // Пошук пацієнтів за ID лікаря
    List<PatientModel> findByDoctor_Id(Long doctorId);
}