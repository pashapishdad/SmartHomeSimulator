import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.apache.commons.lang3.SystemUtils;
import org.flywaydb.core.Flyway;
import service.DatabaseService;
import java.sql.SQLException;

public class MainApplication {

    /**
     * Main method of the project.
     * This will call the method to set up the DB, then it will call the JavaFX controller to start the application
     *
     * @param args Console arguments
     * @throws ManagedProcessException Exception thrown if the DB unexpectedly crashes
     * @throws SQLException            Exception thrown if the DB is unable to start
     */
    public static void main(String[] args) {
        try {
            SetupDatabase();
            JavaFXController.main(args);
        } catch (ManagedProcessException | SQLException e) {
            throw new StartupFailureException(e);
        } finally {
            System.exit(0);
        }
    }

    /**
     * Sets up the DB for this program and migrates all tables automatically from resources/db/migration.
     * Data directory is on the drive's root in a folder named mariaDB
     *
     * @throws ManagedProcessException Exception thrown if the DB unexpectedly crashes
     * @throws SQLException            Exception thrown if the DB is unable to start
     */
    private static void SetupDatabase() throws ManagedProcessException, SQLException {
        DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
        config.setPort(0);
        if (SystemUtils.IS_OS_WINDOWS) {
            config.setDataDir("/mariaDB/backup");
        } else {
            config.setDataDir("~/mariaDB/backup");
        }

        DB db = DB.newEmbeddedDB(config.build());
        db.start();
        db.createDB("smartHome");

        final String databaseUrl = db.getConfiguration().getURL("smartHome") + "?serverTimezone=UTC";
        DatabaseService.SetupDBController(databaseUrl);

        Flyway flyway = Flyway.configure().dataSource(databaseUrl, "root", "").load();
        flyway.migrate();
    }
}
