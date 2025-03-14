package model.io;

public class StepCommand implements Command {
    @Override
    public CommandType getType() {
        return CommandType.STEP;
    }
}
