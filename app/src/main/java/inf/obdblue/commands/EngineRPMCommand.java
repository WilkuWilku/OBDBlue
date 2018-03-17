package inf.obdblue.commands;

/**
 * Created by Inf on 2018-03-15.
 */

public class EngineRPMCommand extends Command{

    public EngineRPMCommand(){
        super(BasicCommands.ENGINE_RPM);
    }

    @Override
    public double convertResponse(double value) {
        return 0;
    }
}
