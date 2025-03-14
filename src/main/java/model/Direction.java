package model;

public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    public static Direction stringToDirection(String stringRepresentationOfDirection) {
        return switch (stringRepresentationOfDirection) {
            case "north" -> NORTH;
            case "east" -> EAST;
            case "south" -> SOUTH;
            case "west" -> WEST;
            default -> throw new IllegalArgumentException("Unexpected direction: " + stringRepresentationOfDirection);
        };
    }
}
