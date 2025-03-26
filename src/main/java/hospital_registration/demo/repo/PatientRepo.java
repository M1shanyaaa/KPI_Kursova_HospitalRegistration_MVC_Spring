package hospital_registration.demo.repo;

import java.util.List;
import hospital_registration.demo.Models.PatientModel;
import hospital_registration.demo.Models.PersonalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepo extends JpaRepository<PatientModel, Long> {
    List<PatientModel> findByDoctor(PersonalModel doctor);
}
