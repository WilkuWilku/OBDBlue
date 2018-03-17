package inf.obdblue.commands;

/**
 * Created by Inf on 2018-03-15.
 */

public abstract class Command implements ValueConverter {
    protected static BasicCommands enumCmd;
    //protected static String command;
    //protected static String description;

    protected Command(BasicCommands enumCmd){
        this.enumCmd = enumCmd;
    }

    public String getDescription(){
        return enumCmd.getDescription();
    }

    public String getCommand(){
        return enumCmd.getCommand();
    }
}
