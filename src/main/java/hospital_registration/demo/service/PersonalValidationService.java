package hospital_registration.demo.service;

import hospital_registration.demo.Models.PersonalModel;
import hospital_registration.demo.repo.PersonalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
public class PersonalValidationService {

    @Autowired
    private PersonalRepo personalRepo;

    public void validatePersonal(PersonalModel personal, BindingResult bindingResult) {
        // Перевірка унікальності логіна
        Optional<PersonalModel> existingPerson = personalRepo.findByLogin(personal.getLogin());
        if (existingPerson.isPresent()) {
            bindingResult.rejectValue("login", "error.login", "Цей логін вже існує. Виберіть інший логін.");
        }

        // Перевірка унікальності email
        Optional<PersonalModel> existingEmail = personalRepo.findByEmail(personal.getEmail());
        if (existingEmail.isPresent()) {
            bindingResult.rejectValue("email", "error.email", "Ця електронна пошта вже використовується.");
        }
    }
}

