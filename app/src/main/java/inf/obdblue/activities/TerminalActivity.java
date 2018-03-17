package inf.obdblue.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import inf.obdblue.BluetoothConnection;
import inf.obdblue.R;


public class TerminalActivity extends AppCompatActivity {

    private Button bSend;
    private TextView tvConsole;
    private EditText etInput;
    private ArrayList<String> consoleLinesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        consoleLinesList = new ArrayList<>();
        bSend = (Button) findViewById(R.id.sendCommandButton);
        tvConsole = (TextView) findViewById(R.id.consoleTextview);
        etInput = (EditText) findViewById(R.id.consoleInputEditText);

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleCommandTask task = new SingleCommandTask();
                task.execute(etInput.getText().toString());
            }
        });
    }





    private class SingleCommandTask extends AsyncTask<String, Void, String> {
        private BluetoothConnection btConnection = BluetoothConnection.getInstance();
        private String command = "No command";
        @Override
        protected String doInBackground(String... params) {
            String response = "NO RESPONSE";
            command = params[0];
            try {
                btConnection.sendMsg(params[0]);
                response = btConnection.readMsg();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            final int CONSOLE_MAX_LINES = tvConsole.getLineCount();
            StringBuilder stringBuilder = new StringBuilder(tvConsole.getText());
            if(consoleLinesList.size() == CONSOLE_MAX_LINES) {
                consoleLinesList.remove(1);
                consoleLinesList.remove(0);
            }
            consoleLinesList.add(command);
            consoleLinesList.add(response);
            stringBuilder.append(command+"\n");
            stringBuilder.append(response+"\n");
            tvConsole.setText(stringBuilder.toString());
        }
    }
}
