package hospital_registration.demo.service;

import hospital_registration.demo.Models.PersonalModel;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public static final String ROLE_MAIN_DOCTOR = "Головний лікар";
    public static final String ROLE_DOCTOR = "Лікар";
    public static final String ROLE_NURSE = "Медсестра/Медбрат";

    /**
     * Перевіряє, чи є користувач головним лікарем
     * @param user користувач для перевірки
     * @return true, якщо користувач - головний лікар
     */
    public boolean isMainDoctor(PersonalModel user) {
        return user != null && ROLE_MAIN_DOCTOR.equals(user.getPosition());
    }

    /**
     * Перевіряє, чи є користувач лікарем
     * @param user користувач для перевірки
     * @return true, якщо користувач - лікар
     */
    public boolean isDoctor(PersonalModel user) {
        return user != null && ROLE_DOCTOR.equals(user.getPosition());
    }

    /**
     * Перевіряє, чи є користувач медсестрою/медбратом
     * @param user користувач для перевірки
     * @return true, якщо користувач - медсестра/медбрат
     */
    public boolean isNurse(PersonalModel user) {
        return user != null && ROLE_NURSE.equals(user.getPosition());
    }

    /**
     * Перевіряє, чи має користувач доступ до сторінки головного лікаря
     * @param user користувач для перевірки
     * @return true, якщо користувач має доступ
     */
    public boolean hasMainDoctorAccess(PersonalModel user) {
        return isMainDoctor(user);
    }

    /**
     * Перевіряє, чи має користувач доступ до сторінки лікаря
     * @param user користувач для перевірки
     * @return true, якщо користувач має доступ
     */
    public boolean hasDoctorAccess(PersonalModel user) {
        return isDoctor(user) || isMainDoctor(user);
    }

    /**
     * Перевіряє, чи має користувач доступ до сторінки медсестри
     * @param user користувач для перевірки
     * @return true, якщо користувач має доступ
     */
    public boolean hasNurseAccess(PersonalModel user) {
        return isNurse(user);
    }
}