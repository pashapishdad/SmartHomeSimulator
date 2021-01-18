package service;

import constants.Position;
import entity.Door;
import entity.Room;
import entity.Window;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * HouseLayoutService class for parsing house layout file
 */
public class HouseLayoutService {

    /**
     * Method creating the rooms for the house with doors, windows, and lights.
     * This method parses the json file inputted by the user
     *
     * @param file The file used to describe the house's rooms
     * @return An array of {@link Room} that comprises the house
     * @throws IOException Thrown if the file cannot be read
     */
    public static Room[] parseHouseLayout(File file) throws IOException {
        String marshalled = Files.readString(file.toPath(), StandardCharsets.US_ASCII);

        JSONObject parsed = new JSONObject(marshalled);

        JSONArray roomsJSON = parsed.getJSONArray("rooms");
        Room[] roomsArray = new Room[roomsJSON.length()];

        for (int i = 0; i < roomsJSON.length(); i++) {
            JSONObject room = roomsJSON.getJSONObject(i);
            String name = roomsJSON.getJSONObject(i).getString("name");
            int lightsTotal = roomsJSON.getJSONObject(i).getInt("lights");

            JSONArray windowsJSON = room.getJSONArray("windows");
            ArrayList<Window> windows = new ArrayList<>();

            JSONArray doorsJSON = room.getJSONArray("doors");
            ArrayList<Door> doors = new ArrayList<>();

            for (int j = 0; j < windowsJSON.length(); j++) {
                int position = windowsJSON.getJSONObject(j).getInt("position");
                windows.add(new Window(getPosition(position), false, false));
            }

            for (int j = 0; j < doorsJSON.length(); j++) {
                int position = doorsJSON.getJSONObject(j).getInt("position");
                String connection = doorsJSON.getJSONObject(j).getString("connection");
                doors.add(new Door(getPosition(position), connection, false));
            }
            roomsArray[i] = new Room.Builder(name).withWindows(windows).withDoors(doors).withlightsTotal(lightsTotal).build();
        }
        return roomsArray;
    }

    /**
     * This function makes an association between an int from 0 to 4 and a Position enum value
     *
     * @param x position index of the door or window
     * @return Position enum value
     */
    public static Position getPosition(int x) {
        switch (x) {
            case 0 -> {
                return Position.NONE;
            }
            case 1 -> {
                return Position.TOP;
            }
            case 2 -> {
                return Position.RIGHT;
            }
            case 3 -> {
                return Position.BOTTOM;
            }
            case 4 -> {
                return Position.LEFT;
            }
        }
        return Position.NONE;
    }

}
