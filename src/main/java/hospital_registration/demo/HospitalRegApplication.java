package hospital_registration.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Головний клас запуску Spring Boot застосунку для системи реєстрації лікарні.
 * <p>
 * Відповідає за ініціалізацію контексту застосунку та запуск вбудованого сервера.
 */
@SpringBootApplication
public class HospitalRegApplication {

	/**
	 * Метод входу в програму.
	 * Ініціалізує Spring Boot застосунок.
	 *
	 * @param args Аргументи командного рядка
	 */
	public static void main(String[] args) {
		SpringApplication.run(HospitalRegApplication.class, args);
	}

}
