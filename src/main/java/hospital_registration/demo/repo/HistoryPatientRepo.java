package hospital_registration.demo.repo;

import hospital_registration.demo.Models.HistoryPatientsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для історії пацієнтів {@link HistoryPatientsModel}.
 * Дозволяє зберігати та витягати архівні дані про пацієнтів.
 */
@Repository
public interface HistoryPatientRepo extends JpaRepository<HistoryPatientsModel, Long> {
}
