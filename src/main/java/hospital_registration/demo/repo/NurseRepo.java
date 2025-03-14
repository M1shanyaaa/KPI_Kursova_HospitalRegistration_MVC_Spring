package hospital_registration.demo.repo;

import hospital_registration.demo.Models.NurseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseRepo extends JpaRepository<NurseModel, Long> {
}