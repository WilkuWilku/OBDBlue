package inf.obdblue;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Inf on 2018-05-02.
 */

public final class Utils {
    public static void saveLogInFile(String filename, Exception e, Context context){
        try {
            OutputStream os = context.openFileOutput(filename, Context.MODE_PRIVATE);
            os.write(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()).getBytes());
            os.write(("\n" + e.getMessage() + "\n").getBytes());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                os.write((stackTraceElement.toString() + "\n").getBytes());
            }
            os.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
