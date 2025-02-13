package hospital_registration.demo.repo;

import hospital_registration.demo.Models.MainDoctorModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MainDoctorRepo extends CrudRepository<MainDoctorModel, Long> {
    Optional<MainDoctorModel> findByLogin(String login);
}
