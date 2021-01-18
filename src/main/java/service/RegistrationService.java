package service;

import constants.RegistrationStatus;
import constants.UserRoles;
import org.apache.commons.lang3.StringUtils;
import java.sql.SQLException;

/**
 * Service layer responsible for user profile management
 */
public class RegistrationService {

    /**
     * Registration Method. It will verify that the given username is unique and that
     *
     * @param username             User's provided username they wish to use.
     * @param firstname            User's chosen first name.
     * @param lastname             User's chosen last name
     * @param password             User's chosen password
     * @param passwordVerification Confirmation of the chosen password
     * @return {@link RegistrationStatus} Status returned by this service to signify status of the new account
     */
    public static RegistrationStatus registration(final String username, final String firstname, final String lastname, final String password, final String passwordVerification) {
        try {
            if (StringUtils.isEmpty(StringUtils.trim(firstname)) || StringUtils.isEmpty(StringUtils.trim(lastname))) {
                return RegistrationStatus.NAME_IS_EMPTY;
            }
            if (!password.equals(passwordVerification)) {
                return RegistrationStatus.PASSWORD_NOT_EQUAL;
            }

            if (DatabaseService.GetNumberOfUsername(username).size() < 1) {
                DatabaseService.createNewUser(username, password, firstname, lastname);
                DatabaseService.createNewUserRole(username, username, UserRoles.PARENT.toString());
                return RegistrationStatus.USER_CREATED;
            } else {
                return RegistrationStatus.NOT_UNIQUE_USERNAME;
            }
        } catch (SQLException e) {
            System.out.println("No users found for given combination");
        }
        return null;
    }

    /**
     * If user has chosen to change passwords, this method will allow them to update it
     *
     * @param username             User's username whose password they want to change
     * @param password             User's new chosen password
     * @param passwordVerification Confirmation of the new password
     * @return {@link RegistrationStatus} Status returned by this service to signify status of the password change
     */
    public static RegistrationStatus updatePassword(final String username, final String password, final String passwordVerification) {
        try {
            if (!password.equals(passwordVerification)) {
                return RegistrationStatus.PASSWORD_NOT_EQUAL;
            }

            if (DatabaseService.GetNumberOfUsername(username).size() == 1) {
                DatabaseService.updateUserPassword(username, password);
                return RegistrationStatus.PASSWORD_UPDATED;
            } else {
                return RegistrationStatus.USERNAME_NOT_FOUND;
            }
        } catch (SQLException e) {
            System.out.println("No users found for given combination");
        }
        return null;
    }

    /**
     * Users can delete a user, and potentially themselves
     * Current session will maintain as long as they do not log out
     *
     * @param username Username related to Profile to delete
     */
    public static void deleteUser(final String userParent, final String username) {
        try {
            DatabaseService.deleteUser(userParent, username);
        } catch (SQLException e) {
            System.out.println("Username does not exist");
        }
    }
}
