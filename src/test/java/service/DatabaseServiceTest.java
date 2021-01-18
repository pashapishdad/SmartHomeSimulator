package service;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import constants.UserRoles;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DatabaseServiceTest {
    static DB db;

    @BeforeAll
    static void setup() throws ManagedProcessException, SQLException {
        DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
        config.setPort(0);

        db = DB.newEmbeddedDB(config.build());
        db.start();
        db.createDB("test");

        final String databaseUrl = db.getConfiguration().getURL("test") + "?serverTimezone=UTC";
        DatabaseService.SetupDBController(databaseUrl);

        Flyway flyway = Flyway.configure().dataSource(databaseUrl, "root", "").load();
        flyway.migrate();
    }

    @AfterAll
    static void cleanup() throws ManagedProcessException {
        db.stop();
    }

    @Test
    public void create_and_get_a_user() throws SQLException {
        DatabaseService.createNewUser("testUser0", "testPassword", "testName", "testName");
        Map<String, Object> result = DatabaseService.verifyLogin("testUser0", "testPassword");

        assertEquals("testName", result.get("firstname"));
        assertEquals("testName", result.get("lastname"));
    }

    @Test
    public void verify_unique_username() throws SQLException {
        DatabaseService.createNewUser("testUser1", "testPassword", "testName", "testName");
        List<String> result = DatabaseService.GetNumberOfUsername("testUser1");
        assertEquals(1, result.size());

        DatabaseService.createNewUser("testUser1", "testPassword", "testName", "testName");
        result = DatabaseService.GetNumberOfUsername("testUser1");
        assertEquals(2, result.size());
    }

    @Test
    public void create_and_update_a_user_password() throws SQLException {
        DatabaseService.createNewUser("testUser2", "testPassword", "testName", "testName");
        Map<String, Object> result = DatabaseService.verifyLogin("testUser2", "testPassword");
        assertEquals("testName", result.get("firstname"));
        assertEquals("testName", result.get("lastname"));

        DatabaseService.updateUserPassword("testUser2", "testPass");
        assertNull(DatabaseService.verifyLogin("testUser2", "testPassword"));
        result = DatabaseService.verifyLogin("testUser2", "testPass");
        assertEquals("testName", result.get("firstname"));
        assertEquals("testName", result.get("lastname"));
    }

    @Test
    public void create_and_update_user_role() throws SQLException {
        DatabaseService.createNewUserRole("testUser3", "testUser3", UserRoles.STRANGER.toString());

        DatabaseService.getAllUserRoles("testUser3").forEach(r -> {
            if (r.getUsername().equals("testUser3")) {
                assertEquals(UserRoles.STRANGER.toString(), r.getRole().toString());
            }
        });

        DatabaseService.updateUserRole("testUser3", "testUser3", UserRoles.GUEST.toString());
        DatabaseService.getAllUserRoles("testUser3").forEach(r -> {
            if (r.getUsername().equals("testUser3")) {
                assertEquals(UserRoles.GUEST.toString(), r.getRole().toString());
            }
        });
    }

    @Test
    public void create_and_delete_a_user() throws SQLException {
        DatabaseService.createNewUserRole("testUser4", "testUser4", "PARENT");
        assertEquals(1, DatabaseService.getAllUserRoles("testUser4").size());
        int numOfAccounts = DatabaseService.query("SELECT username FROM users", new ColumnListHandler<>()).size();

        DatabaseService.deleteUser("testUser4", "testUser4");
        int numOfAccountsAfter = DatabaseService.getAllUserRoles("testUser4").size();
        assertEquals(numOfAccounts - 1, numOfAccountsAfter);
    }
}
