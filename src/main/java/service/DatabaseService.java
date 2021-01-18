package service;

import entity.UserRole;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Database Service which is responsible for all queries to the database.
 */
public class DatabaseService {
    private static Connection databaseConnection;
    private static QueryRunner queryRunner;

    /**
     * Sets up the DB's URL
     *
     * @param databaseUrl Database's URL
     * @throws SQLException Exception thrown if DB cannot be reached
     */
    public static void SetupDBController(String databaseUrl) throws SQLException {
        queryRunner = new QueryRunner();
        databaseConnection = DriverManager.getConnection(databaseUrl, "root", "");
    }

    /**
     * Update SQL command syntax
     *
     * @param command The SQL command
     * @return The number of rows updated
     * @throws SQLException Thrown if the command has a syntax error
     */
    private static int update(String command) throws SQLException {
        return queryRunner.update(databaseConnection, command);
    }

    /**
     * Insert SQL command syntax
     *
     * @param command The SQL command
     * @return The rows added
     * @throws SQLException Thrown if the command has a syntax error
     */
    private static <T> T insert(String command, ResultSetHandler<T> handler) throws SQLException {
        return queryRunner.insert(databaseConnection, command, handler);
    }

    /**
     * Query SQL command syntax
     *
     * @param command The SQL command
     * @return The number of rows queried
     * @throws SQLException Thrown if the command has a syntax error
     */
    protected static <T> T query(String command, ResultSetHandler<T> handler) throws SQLException {
        return queryRunner.query(databaseConnection, command, handler);
    }

    /**
     * The SQL command for login verification
     *
     * @param username Given username for verification
     * @param password Given password for verification
     * @return Map of the firstname and lastname for the valid combination of username and password
     * @throws SQLException Thrown if the command has a syntax error
     */
    public static Map<String, Object> verifyLogin(final String username, final String password) throws SQLException {
        return query("SELECT username, firstname, lastname FROM users WHERE username = \"" + username + "\" AND password = \"" + password + "\"", new MapHandler());
    }

    /**
     * The SQL command for obtaining the number of occurrences of a given username
     *
     * @param username The given username
     * @return List of the same username. Uses the .size() to verify number of occurrences
     * @throws SQLException Thrown if the command has a syntax error
     */
    public static List<String> GetNumberOfUsername(final String username) throws SQLException {
        return query("SELECT username FROM users WHERE username LIKE \"" + username + "\"", new ColumnListHandler<>());
    }

    /**
     * The SQL command for user creation
     *
     * @param username  New user's username
     * @param password  New user's password
     * @param firstname New user's first name
     * @param lastname  New user's last name
     * @return List of Strings containing information about the new user
     * @throws SQLException Thrown if the command has a syntax error
     */
    public static List<String> createNewUser(final String username, final String password, final String firstname, final String lastname) throws SQLException {
        return insert("INSERT INTO users VALUES (\"" + username + "\", \"" + password + "\", \"" + firstname + "\", \"" + lastname + "\")", new ColumnListHandler<>());
    }

    /**
     * The SQL command for updating password
     *
     * @param username User's username whose password will change
     * @param password User's new password
     * @return The number of affected rows
     * @throws SQLException Thrown if the command has a syntax error
     */
    public static int updateUserPassword(final String username, final String password) throws SQLException {
        return update("UPDATE users SET password = \"" + password + "\" WHERE username LIKE \"" + username + "\"");
    }

    /**
     * The SQL command for obtaining all User/Role combination
     *
     * @return A list of {@link UserRole} which contains information about the username and their role
     * @throws SQLException Thrown if the command has a syntax error
     */
    public static List<UserRole> getAllUserRoles(final String userParent) throws SQLException {
        return query("SELECT parentUser, username, role FROM roles WHERE parentUser LIKE \"" + userParent + "\"", new BeanListHandler<UserRole>(UserRole.class));
    }

    /**
     * The SQL command for creating a new user role
     *
     * @param username The given username
     * @param role     The username's first role
     * @return A list of affected rows in the database
     * @throws SQLException Thrown if the command has a syntax error
     */
    public static List<String> createNewUserRole(final String userParent, final String username, final String role) throws SQLException {
        return insert("INSERT INTO roles VALUES (\"" + userParent + "\", \"" + username + "\", \"" + role + "\")", new ColumnListHandler<>());
    }

    /**
     * The SQL command for updating the user's role
     *
     * @param username The given username
     * @param role     The username's new role
     * @return The number of affected rows in the database
     * @throws SQLException Thrown if the command has a syntax error
     */
    public static int updateUserRole(final String userParent, final String username, final String role) throws SQLException {
        return update("UPDATE roles SET role = \"" + role + "\" WHERE username LIKE \"" + username + "\" AND parentUser LIKE \"" + userParent + "\"");
    }

    /**
     * The SQL command for deleting a user from both users and roles tables
     *
     * @param username The to be deleted username
     * @throws SQLException Thrown if the command has a syntax error
     */
    public static void deleteUser(final String userParent, final String username) throws SQLException {
        query("DELETE FROM roles WHERE username LIKE \"" + username + "\" AND parentUser LIKE \"" + userParent + "\"", new ScalarHandler<Integer>());
    }
}
