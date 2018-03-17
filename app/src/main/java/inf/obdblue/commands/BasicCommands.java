package inf.obdblue.commands;

/**
 * Created by Inf on 2018-03-11.
 */

/* Klasa z podstawowymi komendami dla MODE 01 */

public enum BasicCommands implements ValueConverter{
    ENGINE_RPM("Engine RPM", "010C", 2),
    VEHICLE_SPEED("Vehicle speed", "010D", 1);


    BasicCommands(String description, String command, int bytesReturned){
        this.description = description;
        this.command = command;
        this.bytesReturned = bytesReturned;
    }

    private String description;
    private String command;
    private int bytesReturned;

    public String getCommand() {
        return command;
    }
    public String getDescription() {
        return description;
    }

    public int getBytesReturned() {
        return bytesReturned;
    }

    @Override
    public double convertResponse(double value) {
        return value;
    }

}
