package inf.obdblue;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void stringToByteConversion() throws Exception{
        assertArrayEquals(new int[]{36, 59, 28, 250}, ReponseParser.parseToUnsignedBytesArray("41 01 24 3B 1C FA"));
    }
}