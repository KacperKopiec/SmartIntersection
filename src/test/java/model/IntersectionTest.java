package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {
    private Intersection intersection;

    @BeforeEach
    void setUp() {
        intersection = new Intersection();
    }

    @Test
    void testAddVehicle() {
        Vehicle vehicle = new Vehicle("V1", new Road(Direction.NORTH, Direction.SOUTH));
        intersection.addVehicle(vehicle);

        Map<Road, LinkedList<Vehicle>> roadQueues = intersection.getRoadQueues();
        assertTrue(roadQueues.containsKey(vehicle.road()));
        assertEquals(1, roadQueues.get(vehicle.road()).size());
        assertEquals(vehicle, roadQueues.get(vehicle.road()).getFirst());
    }

    @Test
    void testComputeBestSetOfRoads_NoVehicles() {
        List<Road> bestRoads = intersection.computeBestSetOfRoads();
        assertTrue(bestRoads.isEmpty(), "No vehicles should result in an empty best road set.");
    }

    @Test
    void testComputeBestSetOfRoads_WithVehicles() {
        Road road1 = new Road(Direction.NORTH, Direction.SOUTH);
        Road road2 = new Road(Direction.WEST, Direction.EAST);
        Road road3 = new Road(Direction.SOUTH, Direction.NORTH);

        intersection.addVehicle(new Vehicle("V1", road1));
        intersection.addVehicle(new Vehicle("V2", road1));
        intersection.addVehicle(new Vehicle("V3", road2));
        intersection.addVehicle(new Vehicle("V4", road3));

        List<Road> bestRoads = intersection.computeBestSetOfRoads();

        assertFalse(bestRoads.isEmpty());
        for (int i = 0; i < bestRoads.size(); i++) {
            for (int j = i + 1; j < bestRoads.size(); j++) {
                assertFalse(Intersection.roadCollisions.get(bestRoads.get(i)).contains(bestRoads.get(j)),
                        "Selected roads should not have conflicts.");
            }
        }
    }

    @Test
    void testPopVehiclesFromOpenRoads() {
        Road road1 = new Road(Direction.NORTH, Direction.SOUTH);
        Road road2 = new Road(Direction.WEST, Direction.EAST);

        Vehicle vehicle1 = new Vehicle("V1", road1);
        Vehicle vehicle2 = new Vehicle("V2", road2);

        intersection.addVehicle(vehicle1);
        intersection.addVehicle(vehicle2);

        List<Road> bestRoads = List.of(road1, road2);
        List<Vehicle> movingVehicles = intersection.popVehiclesFromOpenRoads(bestRoads);

        assertEquals(2, movingVehicles.size());
        assertTrue(movingVehicles.contains(vehicle1));
        assertTrue(movingVehicles.contains(vehicle2));
        assertTrue(intersection.getRoadQueues().get(road1).isEmpty());
        assertTrue(intersection.getRoadQueues().get(road2).isEmpty());
    }

    @Test
    void testPopVehiclesFromEmptyRoads() {
        Road road = new Road(Direction.NORTH, Direction.SOUTH);

        List<Road> roadsToOpen = List.of(road);
        assertThrows(NullPointerException.class, () -> intersection.popVehiclesFromOpenRoads(roadsToOpen));
    }

    @Test
    void testComputeBestSetOfRoads_WithCollisions() {
        Road road1 = new Road(Direction.SOUTH, Direction.WEST);
        Road road2 = new Road(Direction.WEST, Direction.EAST);

        intersection.addVehicle(new Vehicle("V1", road1));
        intersection.addVehicle(new Vehicle("V2", road2));

        List<Road> bestRoads = intersection.computeBestSetOfRoads();

        assertTrue(bestRoads.contains(road1) ^ bestRoads.contains(road2),
                "Both colliding roads should not be selected together.");
    }

    @Test
    void testComputeBestSetOfRoads_WithMultipleCollisions() {
        Road road1 = new Road(Direction.SOUTH, Direction.NORTH);
        Road road2 = new Road(Direction.EAST, Direction.WEST);
        Road road3 = new Road(Direction.NORTH, Direction.SOUTH);
        Road road4 = new Road(Direction.WEST, Direction.EAST);

        intersection.addVehicle(new Vehicle("V1", road1));
        intersection.addVehicle(new Vehicle("V2", road2));
        intersection.addVehicle(new Vehicle("V3", road3));
        intersection.addVehicle(new Vehicle("V4", road4));

        List<Road> bestRoads = intersection.computeBestSetOfRoads();

        assertFalse(bestRoads.contains(road1) && bestRoads.contains(road2),
                "Conflicting roads road1 and road3 should not be selected together.");
        assertFalse(bestRoads.contains(road3) && bestRoads.contains(road4),
                "Conflicting roads road2 and road4 should not be selected together.");
    }

    @Test
    void testComputeBestSetOfRoads_ThenAddVehicles_ThenRecompute() {
        Road road1 = new Road(Direction.NORTH, Direction.SOUTH);
        intersection.addVehicle(new Vehicle("V1", road1));

        List<Road> firstBestSet = intersection.computeBestSetOfRoads();
        assertEquals(1, firstBestSet.size());
        assertTrue(firstBestSet.contains(road1));

        Road road2 = new Road(Direction.WEST, Direction.EAST);
        intersection.addVehicle(new Vehicle("V2", road2));

        List<Road> secondBestSet = intersection.computeBestSetOfRoads();
        assertEquals(1, secondBestSet.size());
    }
}
