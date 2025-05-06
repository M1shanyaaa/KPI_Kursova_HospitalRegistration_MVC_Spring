package hospital_registration.demo.repo;

import hospital_registration.demo.Models.HistoryPatientsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HistoryPatientRepo extends JpaRepository<HistoryPatientsModel, Long> {
}