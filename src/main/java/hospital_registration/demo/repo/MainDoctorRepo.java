package hospital_registration.demo.repo;

import hospital_registration.demo.Models.DoctorModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MainDoctorRepo extends CrudRepository<DoctorModel, Long> {
    Optional<DoctorModel> findByLogin(String login);
}
