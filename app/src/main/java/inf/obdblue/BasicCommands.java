package inf.obdblue;

/**
 * Created by Inf on 2018-03-11.
 */

/* Klasa z podstawowymi komendami dla MODE 01 */

public enum BasicCommands {
    ENGINE_RPM("Engine RPM", "010C"),
    VEHICLE_SPEED("Vehicle speed", "010D");


    BasicCommands(String description, String command){
        this.description = description;
        this.command = command;
    }

    private String description;
    private String command;

    public String getCommand() {
        return command;
    }
    public String getDescription() {
        return description;
    }
}
