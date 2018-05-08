package inf.obdblue.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import inf.obdblue.BluetoothConnection;
import inf.obdblue.R;
import inf.obdblue.Utils;


public class TerminalActivity extends AppCompatActivity {
    public static final String LogfileName = "terminal_logfile";
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
                /* wyślij komendę do urządzenia */
                btConnection.sendMsg(params[0]);
                /* odczytaj odpowiedź */
                response = btConnection.readMsg();
                /* wyświetl odpowiedź */
                Toast.makeText(getApplicationContext(), "RESP: "+response, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Utils.saveLogInFile(LogfileName, e, getApplicationContext());
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            final int CONSOLE_MAX_LINES = 20;
            /* usuń dwa ostatnie wpisy */
            if(consoleLinesList.size() > CONSOLE_MAX_LINES-1) {
                consoleLinesList.remove(1);
                consoleLinesList.remove(0);
            }
            StringBuilder stringBuilder = new StringBuilder(linesListToString());
            /* dodaj i wypisz nową komendę oraz odpowiedź */
            consoleLinesList.add(">> "+command);
            consoleLinesList.add(response);
            stringBuilder.append(">> "+command+"\n");
            stringBuilder.append(response+"\n");
            tvConsole.setText(stringBuilder.toString());
        }
    }

    /* zamień linijki w terminalu na jednego Stringa */
    private String linesListToString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(String line : consoleLinesList)
            stringBuilder.append(line+"\n");
        return stringBuilder.toString();
    }
}
