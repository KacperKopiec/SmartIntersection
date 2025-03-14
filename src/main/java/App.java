import model.Simulation;
import model.io.Command;
import model.io.InputParser;

import java.io.IOException;
import java.util.List;

public class App {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Wrong number of arguments.");
            System.exit(0);
        }

        try {
            List<Command> commands = InputParser.parseFile(args[0]);
            Simulation simulation = new Simulation(commands, args[1]);
            simulation.start();
        } catch (IOException e) {
            System.err.println("Failed to load commands: " + e.getMessage());
            System.exit(0);
        }
    }
}