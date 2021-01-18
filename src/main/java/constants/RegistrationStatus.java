package constants;

import service.RegistrationService;

/**
 * All possible status returned by {@link RegistrationService}
 */
public enum RegistrationStatus {
    USER_CREATED, NOT_UNIQUE_USERNAME, PASSWORD_NOT_EQUAL, PASSWORD_UPDATED, USERNAME_NOT_FOUND, NAME_IS_EMPTY
}
