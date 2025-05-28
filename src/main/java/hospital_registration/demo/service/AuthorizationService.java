package hospital_registration.demo.service;

import hospital_registration.demo.Models.PersonalModel;
import org.springframework.stereotype.Service;

/**
 * Сервіс авторизації, який визначає роль користувача
 * та перевіряє доступ до різних частин системи.
 * <p>
 * Працює з об'єктами {@link PersonalModel}, де визначено посаду користувача.
 * </p>
 */
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
        return user != null && user.getPosition() != null &&
                ROLE_MAIN_DOCTOR.equalsIgnoreCase(user.getPosition().trim());
    }

    /**
     * Перевіряє, чи є користувач лікарем (звичайним, не головним)
     * @param user користувач для перевірки
     * @return true, якщо користувач - лікар
     */
    public boolean isDoctor(PersonalModel user) {
        return user != null && user.getPosition() != null &&
                ROLE_DOCTOR.equalsIgnoreCase(user.getPosition().trim());
    }

    /**
     * Перевіряє, чи є користувач будь-яким типом лікаря (включаючи головного)
     * @param user користувач для перевірки
     * @return true, якщо користувач - лікар або головний лікар
     */
    public boolean isAnyDoctor(PersonalModel user) {
        return isDoctor(user) || isMainDoctor(user) ||
                (user != null && user.getPosition() != null &&
                        user.getPosition().toLowerCase().contains("лікар"));
    }

    /**
     * Перевіряє, чи є користувач медсестрою/медбратом
     * @param user користувач для перевірки
     * @return true, якщо користувач - медсестра/медбрат
     */
    public boolean isNurse(PersonalModel user) {
        return user != null && user.getPosition() != null &&
                (ROLE_NURSE.equalsIgnoreCase(user.getPosition().trim()) ||
                        user.getPosition().toLowerCase().contains("сестр"));
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
        return isAnyDoctor(user);
    }

    /**
     * Перевіряє, чи має користувач доступ до сторінки медсестри
     * @param user користувач для перевірки
     * @return true, якщо користувач має доступ
     */
    public boolean hasNurseAccess(PersonalModel user) {
        return isNurse(user);
    }

    /**
     * Перевіряє, чи має користувач право керувати персоналом
     * @param user користувач для перевірки
     * @return true, якщо користувач може керувати персоналом
     */
    public boolean canManagePersonal(PersonalModel user) {
        return isMainDoctor(user);
    }

    /**
     * Перевіряє, чи може користувач видаляти конкретного співробітника
     * @param currentUser поточний користувач
     * @param targetUser користувач, якого планується видалити
     * @return true, якщо видалення дозволено
     */
    public boolean canDeletePersonal(PersonalModel currentUser, PersonalModel targetUser) {
        if (currentUser == null || targetUser == null) {
            return false;
        }

        // Не можна видаляти самого себе
        if (currentUser.getId().equals(targetUser.getId())) {
            return false;
        }

        // Тільки головний лікар може видаляти персонал
        return isMainDoctor(currentUser);
    }

    /**
     * Отримує роль користувача як рядок
     * @param user користувач
     * @return роль користувача або "Невідома роль"
     */
    public String getUserRole(PersonalModel user) {
        if (user == null || user.getPosition() == null) {
            return "Невідома роль";
        }

        if (isMainDoctor(user)) {
            return "Головний лікар";
        } else if (isDoctor(user)) {
            return "Лікар";
        } else if (isNurse(user)) {
            return "Медсестра/Медбрат";
        } else {
            return user.getPosition();
        }
    }

    /**
     * Перевіряє, чи користувач увійшов в систему
     * @param user користувач для перевірки
     * @return true, якщо користувач не null
     */
    public boolean isAuthenticated(PersonalModel user) {
        return user != null;
    }
}