package controller;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import javafx.fxml.FXMLLoader;
import org.apache.commons.io.FileUtils;
import org.flywaydb.core.Flyway;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import service.DatabaseService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserRolesControllerTest extends ApplicationTest {

    FXMLLoader loader;
    UserRolesController userRolesController;
    static DB db;

    @BeforeAll
    static void setupDB() throws ManagedProcessException, SQLException, IOException {
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

    @BeforeEach
    public void setup() throws IOException {
        loader = new FXMLLoader(getClass().getResource("/view/userRoles.fxml"));
        loader.load();
        userRolesController = loader.getController();

    }

    /**
     * Use Case 2, Delivery 2
     * @throws IOException
     */
    @Test
    public void should_export_user_profiles() throws IOException {
        File file = FileUtils.getFile("src", "test", "resources", "testExport.txt");

        JSONArray jsonArray = new JSONArray();
        JSONObject object = new JSONObject();

        object.put("username", "username");
        object.put("role", "role");

        jsonArray.put(object);

        JSONObject toExport = new JSONObject();
        toExport.put("users", jsonArray);
        toExport.put("permissions", "");

        userRolesController.saveToFile(toExport, file);

        assertEquals("{\"permissions\":\"\",\"users\":[{\"role\":\"role\",\"username\":\"username\"}]}\n".replaceAll("\n", "").replaceAll("\r", ""), Files.readString(file.toPath()).replaceAll("\n", "").replaceAll("\r", ""));
    }
}
