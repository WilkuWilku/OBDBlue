package inf.obdblue.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import inf.obdblue.R;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvLogs = (TextView) findViewById(R.id.logTextView);
        tvLogs.setMovementMethod(new ScrollingMovementMethod());
        File dashboardLogfile = new File(getFilesDir(), DashboardActivity.logfileName);
        File btConnLogfile = new File(getFilesDir(), ConnectionActivity.logfileName);
        File terminalLogfile = new File(getFilesDir(), TerminalActivity.logfileName);
        /* odczyt błędów zebranych w pliku */
        readLog(dashboardLogfile, "--- DASHBOARD ACTIVITY LOG ---\n");
        readLog(btConnLogfile, "--- CONNECTION ACTIVITY LOG ---\n");
        readLog(terminalLogfile, "--- TERMINAL ACTIVITY LOG ---\n");
    }

    private void readLog(File file, String title){
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder(title);
            String line = "";
            while((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line+"\n");
            String currentLog = tvLogs.getText().toString();
            tvLogs.setText(currentLog+"\n\n"+stringBuilder.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


