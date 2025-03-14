package model.io;

import model.Direction;
import model.Road;
import model.Vehicle;

public record AddVehicleCommand(String vehicleId, String startRoad, String endRoad) implements Command {

    @Override
    public CommandType getType() {
        return CommandType.ADD_VEHICLE;
    }

    public Vehicle createVehicle() {
        return new Vehicle(this.vehicleId, new Road(Direction.stringToDirection(startRoad), Direction.stringToDirection(endRoad)));
    }
}
