package model.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Vehicle;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OutputWriter {
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void write(String filePath, List<Vehicle> vehicleList) {
        List<String> vehicleIdList = new LinkedList<>();
        for (Vehicle vehicle : vehicleList) {
            vehicleIdList.add(vehicle.vehicleId());
        }

        List<Map<String, List<String>>> stepStatuses = readExistingData(filePath);
        stepStatuses.add(Map.of("leftVehicles", vehicleIdList));

        try (FileWriter file = new FileWriter(filePath)) {
            GSON.toJson(Map.of("stepStatuses", stepStatuses), file);
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + filePath);
        }
    }

    private static List<Map<String, List<String>>> readExistingData(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Map<String, List<Map<String, List<String>>>> existingData = GSON.fromJson(reader, Map.class);
            if (existingData != null && existingData.containsKey("stepStatuses")) {
                return existingData.get("stepStatuses");
            }
        } catch (IOException e) {
            // File might not exist yet, return empty list
        }
        return new LinkedList<>();
    }
}
