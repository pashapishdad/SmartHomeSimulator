package service;

import java.sql.SQLException;
import java.util.Map;

/**
 * Service layer responsible for authenticating the user
 */
public class LoginService {
    /**
     * Logging in method. It will call the {@link DatabaseService} to look for the username/password combination.
     *
     * @param username The user's username.
     * @param password The user's password related to the given username.
     * @return A map of all combination for given username and password. It is assumed to be of a single combination.
     */
    public static Map<String, Object> login(final String username, final String password) {
        try {
            return DatabaseService.verifyLogin(username, password);
        } catch (SQLException e) {
            System.out.println("No users found for given combination");
        }
        return null;
    }
}
