package inf.obdblue.commands;

/**
 * Created by Inf on 2018-03-15.
 */

public interface ValueConverter {
    double convertResponse(int byteA, int byteB, int byteC, int byteD);

}
