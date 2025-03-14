package model;

import model.io.AddVehicleCommand;
import model.io.Command;
import model.io.OutputWriter;

import java.util.List;

public class Simulation extends Thread {
    private final Intersection intersection = new Intersection();
    private final List<Command> commandList;
    private final String outputFilePath;

    public Simulation(List<Command> commandList, String outputFilePath) {
        this.commandList = commandList;
        this.outputFilePath = outputFilePath;
    }

    private void step() {
        List<Road> openRoads = intersection.computeBestSetOfRoads();
        List<Vehicle> movingVehicles = intersection.popVehiclesFromOpenRoads(openRoads);
        OutputWriter.write(outputFilePath, movingVehicles);
    }

    private void addVehicle(AddVehicleCommand command) {
        intersection.addVehicle(command.createVehicle());
    }

    @Override
    public void run() {
        for (Command command : commandList) {
            switch (command.getType()) {
                case STEP -> step();
                case ADD_VEHICLE -> addVehicle((AddVehicleCommand) command);
            }
        }
    }
}
