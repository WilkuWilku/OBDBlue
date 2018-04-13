package inf.obdblue;

import java.math.BigInteger;

/**
 * Created by Inf on 2018-03-14.
 */

/* Klasa zamieniająca odpowiedź urządzenia na zrozumiałą daną */

public final class ReponseParser {

    public static int[] parseToUnsignedBytesArray(String response){
        /* pierwszy podłańcuch to "SEARCHING...", a dwa kolejne bajty nie należą do wartości */
        final int OFFSET = response.startsWith("SEARCHING") ? 3 : 2;
        /* podział na bajty */
        String[] stringBytes = response.split("\\s+");
        /* odpowiedź "NO DATA" */
        if(stringBytes[0].toLowerCase() == "no")
            return new int[]{-1,0,0,0};
        /* parsowanie na bajty ze znakiem */
        byte[] signedBytes = new byte[stringBytes.length-OFFSET];
        for(int i=0; i<stringBytes.length-OFFSET; i++)
            signedBytes[i] = new BigInteger(stringBytes[i+OFFSET], 16).byteValue();
        /* konwersja na 4 bajty bez znaku */
        int[] unsignedBytes = new int[4];
        for(int i=0; i<signedBytes.length; i++)
            unsignedBytes[i] = signedBytes[i] & 0xFF;
        return unsignedBytes;
    }
}
