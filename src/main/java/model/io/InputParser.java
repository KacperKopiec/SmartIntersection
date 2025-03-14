package model.io;

import com.google.gson.*;
import model.Direction;
import model.Road;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class InputParser {
    public static List<Command> parseFile(String filePath) throws IOException {
        List<Command> commandList = new LinkedList<>();

        try (FileReader reader = new FileReader(filePath)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray commandsArray = jsonObject.getAsJsonArray("commands");

            for (JsonElement element : commandsArray) {
                JsonObject obj = element.getAsJsonObject();
                String type = obj.get("type").getAsString();

                if ("step".equals(type)) {
                    commandList.add(new StepCommand());
                } else if ("addVehicle".equals(type)) {
                    String vehicleId = obj.get("vehicleId").getAsString();
                    String startRoad = obj.get("startRoad").getAsString();
                    String endRoad = obj.get("endRoad").getAsString();
                    commandList.add(new AddVehicleCommand(vehicleId, startRoad, endRoad));
                } else System.err.println("Warning: Unknown command type: " + type);
            }
        } catch (IOException e) {
            throw new IOException("Error reading file: " + filePath, e);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Invalid JSON format in file: " + filePath, e);
        }

        return commandList;
    }
}
