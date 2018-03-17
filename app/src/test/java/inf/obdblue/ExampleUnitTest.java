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
    public void StringToByteConversion() throws Exception{
        //assertArrayEquals(new byte[]{0xFF, 0x3B, 0x1C}, ReponseParser.splitToBytes("41 01 24 3B 1C", 5));
    }
}