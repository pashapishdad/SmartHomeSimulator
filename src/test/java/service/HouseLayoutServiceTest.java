package service;

import entity.Room;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

public class HouseLayoutServiceTest {

    @Test
    public void should_create_array_of_rooms() throws IOException {
        List<String> roomNames = Arrays.asList("Bathroom", "Living Room", "Bedroom 1", "Bedroom 2", "Kitchen", "Backyard", "Entrance", "Garage");

        File file = FileUtils.getFile("src", "test", "resources", "houseLayout.txt");
        List<Room> rooms = Arrays.asList(HouseLayoutService.parseHouseLayout(file));

        rooms.stream().forEach(room -> {
            assertThat(roomNames, hasItems(room.getName()));
        });
    }
}
