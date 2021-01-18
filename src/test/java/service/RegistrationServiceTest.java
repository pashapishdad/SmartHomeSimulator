package service;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import constants.RegistrationStatus;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RegistrationServiceTest {
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
    public void register_successfully() {
        RegistrationStatus status = RegistrationService.registration("testUser1", "testUser1", "testUser1", "testUser1", "testUser1");
        assertEquals(RegistrationStatus.USER_CREATED, status);
    }

    @Test
    public void not_equal_password() {
        RegistrationStatus status = RegistrationService.registration("testUser2", "testUser1", "testUser1", "testUser1", "wrong");
        assertEquals(RegistrationStatus.PASSWORD_NOT_EQUAL, status);
    }

    @Test
    public void not_unique_username() throws SQLException {
        RegistrationService.registration("testUser2", "testUser1", "testUser1", "testUser1", "testUser1");
        RegistrationStatus status = RegistrationService.registration("testUser2", "testUser1", "testUser1", "testUser1", "testUser1");
        assertEquals(RegistrationStatus.NOT_UNIQUE_USERNAME, status);

        assertEquals(DatabaseService.GetNumberOfUsername("testUser2").size(), 1);
    }

    @Test
    public void no_firstname() {
        RegistrationStatus status = RegistrationService.registration("testUser2", null, null, "testUser1", "testUser1");
        assertEquals(RegistrationStatus.NAME_IS_EMPTY, status);
    }

    @Test
    public void spaces_as_firstname() {
        RegistrationStatus status = RegistrationService.registration("testUser2", "    ", "correct", "testUser1", "testUser1");
        assertEquals(RegistrationStatus.NAME_IS_EMPTY, status);
    }

    @Test
    public void update_password() throws SQLException {
        RegistrationService.registration("testUser3", "testUser1", "testUser1", "testUser1", "testUser1");
        RegistrationStatus status = RegistrationService.updatePassword("testUser3", "test", "test");
        assertEquals(RegistrationStatus.PASSWORD_UPDATED, status);

        assertEquals(DatabaseService.GetNumberOfUsername("testUser3").size(), 1);
        assertNotNull(DatabaseService.verifyLogin("testUser3", "test"));
    }

    @Test
    public void cannot_update_unknown_username_password() {
        RegistrationStatus status = RegistrationService.updatePassword("testUser4", "test", "test");
        assertEquals(RegistrationStatus.USERNAME_NOT_FOUND, status);
    }
}
