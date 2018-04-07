package inf.obdblue.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import inf.obdblue.R;
import inf.obdblue.commands.BasicCommands;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvLogs = (TextView) findViewById(R.id.logTextView);
        tvLogs.setMovementMethod(new ScrollingMovementMethod());
        File file = new File(getFilesDir(), DashboardActivity.logFileName);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder("### DASHBOARD LOG ###\n");
            String line = "";
            while((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line+"\n");
            tvLogs.setText(stringBuilder.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
