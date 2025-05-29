package hospital_registration.demo.service;

import hospital_registration.demo.Models.PatientModel;
import hospital_registration.demo.repo.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * Сервіс для валідації даних пацієнта.
 * <p>
 * Перевіряє бізнес-правила при створенні або редагуванні пацієнта, а саме:
 * <ul>
 *     <li>Дата народження не може бути пізнішою за дату запису.</li>
 *     <li>Дата запису не може бути пізнішою за дату виписки.</li>
 *     <li>Ліжко в палаті не може бути зайняте двома пацієнтами в один і той самий період.</li>
 * </ul>
 * Для додаткових повідомлень про помилки використовується {@link BindingResult}.
 */

@Service
public class PatientValidationService {

    @Autowired
    private PatientRepo patientRepository;

    /**
     * Перевіряє відповідність дат бізнес-логіці:
     * - дата народження має бути не пізніше дати запису
     * - дата запису має бути не пізніше дати виписки
     *
     * @param patient        пацієнт для перевірки
     * @param bindingResult  об’єкт для зберігання помилок
     */
    public void validateDates(PatientModel patient, BindingResult bindingResult) {
        if (patient.getBirthDate() != null && patient.getAppointmentDateFrom() != null) {
            if (patient.getBirthDate().isAfter(patient.getAppointmentDateFrom().toLocalDate())) {
                bindingResult.rejectValue("birthDate", "error.birthDate",
                        "Дата народження не може бути після дати запису.");
            }
        }

        if (patient.getAppointmentDateFrom() != null && patient.getAppointmentDateTo() != null) {
            if (patient.getAppointmentDateFrom().isAfter(patient.getAppointmentDateTo())) {
                bindingResult.rejectValue("appointmentDateFrom", "error.appointmentDateFrom",
                        "Дата запису не може бути пізніше за дату виписки.");
            }
        }

        if (patient.getWard() != null && patient.getBed() != null &&
                patient.getAppointmentDateFrom() != null && patient.getAppointmentDateTo() != null) {

            List<PatientModel> conflicts = patientRepository.findConflictingPatients(
                    patient.getWard(), patient.getBed(),
                    patient.getAppointmentDateFrom(), patient.getAppointmentDateTo()
            );

            if (patient.getId() != null) {
                conflicts = conflicts.stream()
                        .filter(p -> !p.getId().equals(patient.getId()))
                        .toList();
            }

            if (!conflicts.isEmpty()) {
                bindingResult.rejectValue("bed", "error.bed",
                        "Це ліжко вже зайняте на вказаний період.");
            }
        }
    }


}
