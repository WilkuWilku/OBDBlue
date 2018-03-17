package inf.obdblue;

/**
 * Created by Inf on 2018-03-14.
 */

/* Klasa zamieniająca odpowiedź urządzenia na zrozumiałą daną */

public final class ReponseParser {
    public static String parse(String response){
        //TODO dokończyć funkcję parsującą
        return response;
    }
    public static int[] splitToBytes(String response, int bytesReturned){
        final int OFFSET = 2;
        String[] stringBytes = response.split("\\s+");
        byte[] result = new byte[stringBytes.length-OFFSET];
        for(int i=0; i<stringBytes.length-OFFSET; i++);
        return null;
    }
}
