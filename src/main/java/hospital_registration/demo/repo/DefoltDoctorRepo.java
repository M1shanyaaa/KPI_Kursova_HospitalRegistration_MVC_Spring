package hospital_registration.demo.repo;

import hospital_registration.demo.Models.DefoltDoctorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefoltDoctorRepo extends JpaRepository<DefoltDoctorModel, Long> {
}
