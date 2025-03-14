package model;

import java.util.*;

public class Intersection {
    private final Map<Road, Integer> roadPriority = new HashMap<>();
    private final Map<Road, LinkedList<Vehicle>> roadQueues = new HashMap<>();
    private static final List<Road> ALL_ROADS = new ArrayList<>();
    static {
        for (Direction from : Direction.values()) {
            for (Direction to : Direction.values()) {
                if (from != to) {
                    ALL_ROADS.add(new Road(from, to));
                }
            }
        }
    }
    // Road_A is in roadCollisions[Road_B] if and only if they cannot have GREEN LIGHTS at the same time
    static final Map<Road, Set<Road>> roadCollisions = new HashMap<>();
    // It is precomputed, won't work for different intersection model, only for model drawn in README
    static {
        List<Road[]> collisions = List.of(
                new Road[]{new Road(Direction.SOUTH, Direction.WEST), new Road(Direction.WEST, Direction.EAST)},
                new Road[]{new Road(Direction.SOUTH, Direction.WEST), new Road(Direction.WEST, Direction.NORTH)},
                new Road[]{new Road(Direction.SOUTH, Direction.WEST), new Road(Direction.NORTH, Direction.SOUTH)},
                new Road[]{new Road(Direction.SOUTH, Direction.WEST), new Road(Direction.EAST, Direction.SOUTH)},
                new Road[]{new Road(Direction.SOUTH, Direction.NORTH), new Road(Direction.WEST, Direction.EAST)},
                new Road[]{new Road(Direction.SOUTH, Direction.NORTH), new Road(Direction.NORTH, Direction.EAST)},
                new Road[]{new Road(Direction.SOUTH, Direction.NORTH), new Road(Direction.EAST, Direction.WEST)},
                new Road[]{new Road(Direction.SOUTH, Direction.NORTH), new Road(Direction.EAST, Direction.SOUTH)},
                new Road[]{new Road(Direction.EAST, Direction.SOUTH), new Road(Direction.WEST, Direction.EAST)},
                new Road[]{new Road(Direction.EAST, Direction.SOUTH), new Road(Direction.NORTH, Direction.EAST)},
                new Road[]{new Road(Direction.EAST, Direction.WEST), new Road(Direction.NORTH, Direction.EAST)},
                new Road[]{new Road(Direction.EAST, Direction.WEST), new Road(Direction.NORTH, Direction.SOUTH)},
                new Road[]{new Road(Direction.EAST, Direction.WEST), new Road(Direction.WEST, Direction.NORTH)},
                new Road[]{new Road(Direction.NORTH, Direction.EAST), new Road(Direction.WEST, Direction.NORTH)},
                new Road[]{new Road(Direction.NORTH, Direction.SOUTH), new Road(Direction.WEST, Direction.NORTH)},
                new Road[]{new Road(Direction.NORTH, Direction.SOUTH), new Road(Direction.WEST, Direction.EAST)}
        );

        for (Road[] pair : collisions) {
            Road road1 = pair[0];
            Road road2 = pair[1];

            roadCollisions.computeIfAbsent(road1, k -> new HashSet<>()).add(road2);
            roadCollisions.computeIfAbsent(road2, k -> new HashSet<>()).add(road1);
        }
    }

    public void addVehicle(Vehicle vehicle) {
        roadQueues.computeIfAbsent(vehicle.road(), k -> new LinkedList<>()).add(vehicle);
    }

    private void setPriorityForRoads() {
        // Algorithm depends on this mapping function
        for (Road road : ALL_ROADS) {
            List<Vehicle> queue = roadQueues.computeIfAbsent(road, k -> new LinkedList<>());
            roadPriority.put(road, queue.isEmpty() ? -1 : queue.size());
        }
    }

    public List<Road> computeBestSetOfRoads() {
        // Computes maximum weight independent set in O(2^n * n^2) where n is number of sources roads
        setPriorityForRoads();
        int bestSet = 0, bestSum = 0;
        for (int binarySet = 1; binarySet < (1 << ALL_ROADS.size()); binarySet++) {
            // sum of priorities
            int sum = 0;
            for (int roadIndex = 0; roadIndex < ALL_ROADS.size(); roadIndex++) {
                if ((binarySet & (1 << roadIndex)) != 0) sum += roadPriority.get(ALL_ROADS.get(roadIndex));
            }
            if (sum <= bestSum) continue;

            // check for collisions
            boolean collision = false;
            for (int road1Index = 0; road1Index < ALL_ROADS.size() - 1; road1Index++) if ((binarySet & (1 << road1Index)) != 0) {
                for (int road2Index = road1Index + 1; road2Index < ALL_ROADS.size(); road2Index++) if ((binarySet & (1 << road2Index)) != 0) {
                    if (roadCollisions.get(ALL_ROADS.get(road1Index)).contains(ALL_ROADS.get(road2Index))) {
                        collision = true;
                        break;
                    }
                }
                if (collision) break;
            }
            if (!collision) {
                bestSet = binarySet;
                bestSum = sum;
            }
        }

        List<Road> bestSetOfRoads = new LinkedList<>();
        for (int roadIndex = 0; roadIndex < ALL_ROADS.size(); roadIndex++) {
            if ((bestSet & (1 << roadIndex)) != 0) bestSetOfRoads.add(ALL_ROADS.get(roadIndex));
        }
        return bestSetOfRoads;
    }

    public List<Vehicle> popVehiclesFromOpenRoads(List<Road> listOfRoads) {
        List<Vehicle> movingVehicles = new LinkedList<>();
        for (Road road : listOfRoads) {
            movingVehicles.add(roadQueues.get(road).removeFirst());
        }
        return movingVehicles;
    }

    public Map<Road, LinkedList<Vehicle>> getRoadQueues() {
        return roadQueues;
    }
}
