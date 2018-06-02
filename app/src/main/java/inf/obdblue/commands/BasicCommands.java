package inf.obdblue.commands;

/**
 * Created by Inf on 2018-03-11.
 */

/* Klasa z podstawowymi komendami dla MODE 01 */

public enum BasicCommands implements ValueConverter{
    ENGINE_RPM("Obroty silnika", "010C", "obr/min", 2){
        @Override
        public double convertResponse(int byteA, int byteB, int byteC, int byteD) {
            return (256*byteA+byteB)/4;
        }
    },
    VEHICLE_SPEED("Prędkość pojazdu", "010D", "km/h", 1){
        @Override
        public double convertResponse(int byteA, int byteB, int byteC, int byteD) {
            return byteA;
        }
    };


    BasicCommands(String description, String command, String units, int nDataBytes){
        this.description = description;
        this.command = command;
        this.units = units;
        this.nDataBytes = nDataBytes;

    }

    private String description;
    private String command;
    private String units;
    private int nDataBytes;

    public String getCommand() {
        return command;
    }
    public String getDescription() {
        return description;
    }
    public String getUnits() {
        return units;
    }

    public int getnDataBytes() {
        return nDataBytes;
    }

    @Override
    public double convertResponse(int byteA, int byteB, int byteC, int byteD) {
        return 0;
    }
}
