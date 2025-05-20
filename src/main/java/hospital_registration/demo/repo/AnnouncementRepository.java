package hospital_registration.demo.repo;

import hospital_registration.demo.Models.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з оголошеннями {@link Announcement}.
 * Надає базові CRUD-операції через інтерфейс JpaRepository.
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}
