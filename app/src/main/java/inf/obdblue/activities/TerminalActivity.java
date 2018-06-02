package inf.obdblue.activities;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import inf.obdblue.BluetoothConnection;
import inf.obdblue.ParamsListItem;
import inf.obdblue.R;
import inf.obdblue.ReponseParser;
import inf.obdblue.Utils;
import inf.obdblue.commands.BasicCommands;


public class TerminalActivity extends AppCompatActivity {
    public static final String logfileName = "terminal_logfile";
    private static final int TERMINAL_DATA = 421;
    private Button bSend;
    private TextView tvConsole;
    private EditText etInput;
    private ArrayList<String> consoleLinesList;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        consoleLinesList = new ArrayList<>();
        bSend = (Button) findViewById(R.id.sendCommandButton);
        tvConsole = (TextView) findViewById(R.id.consoleTextview);
        etInput = (EditText) findViewById(R.id.consoleInputEditText);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String command = bundle.getString("COMMAND");
                String response = bundle.getString("RESPONSE");
                switch (msg.what){
                    case TERMINAL_DATA:
                        final int CONSOLE_MAX_LINES = 20;
                                /* usuń dwa ostatnie wpisy */
                        if(consoleLinesList.size() > CONSOLE_MAX_LINES-1) {
                            consoleLinesList.remove(1);
                            consoleLinesList.remove(0);
                        }
                        StringBuilder stringBuilder = new StringBuilder(linesListToString());
                                /* dodaj i wypisz nową komendę oraz odpowiedź */
                        try {
                            consoleLinesList.add(">> " + command);
                            consoleLinesList.add(response);
                            stringBuilder.append(">> " + command + "\n");
                            stringBuilder.append(response + " # Bytes av: " + BluetoothConnection.getInstance().getBTSocket().getInputStream().available() + "\n");
                            tvConsole.setText(stringBuilder.toString());
                        } catch (IOException e){
                            e.printStackTrace();
                            Utils.saveLogInFile(logfileName, e, getApplicationContext());
                        }
                        Toast.makeText(getApplicationContext(), "RESP: "+response, Toast.LENGTH_SHORT).show();
                        break;
                    default: super.handleMessage(msg); break;
                }
                super.handleMessage(msg);
            }
        };

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SingleCommandTask task = new SingleCommandTask();
                //task.execute(etInput.getText().toString());
                new TerminalThread(etInput.getText().toString(), 2).start();
            }
        });
    }

//    private class SingleCommandTask extends AsyncTask<String, Void, String> {
//        private BluetoothConnection btConnection = BluetoothConnection.getInstance();
//        private String command = "No command";
//
//        @Override
//        protected String doInBackground(String... params) {
//            String response = "NO RESPONSE";
//            command = params[0];
//            try {
//                /* wyślij komendę do urządzenia */
//                btConnection.sendMsg(params[0]);
//                /* odczytaj odpowiedź */
//                response = btConnection.readMsg();
//                /* wyświetl odpowiedź */
//                Toast.makeText(getApplicationContext(), "RESP: "+response, Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                Utils.saveLogInFile(logfileName, e, getApplicationContext());
//            }
//            return response;
//        }
//
//        @Override
//        protected void onPostExecute(String response) {
//            final int CONSOLE_MAX_LINES = 20;
//            /* usuń dwa ostatnie wpisy */
//            if(consoleLinesList.size() > CONSOLE_MAX_LINES-1) {
//                consoleLinesList.remove(1);
//                consoleLinesList.remove(0);
//            }
//            StringBuilder stringBuilder = new StringBuilder(linesListToString());
//            /* dodaj i wypisz nową komendę oraz odpowiedź */
//            consoleLinesList.add(">> "+command);
//            consoleLinesList.add(response);
//            stringBuilder.append(">> "+command+"\n");
//            stringBuilder.append(response+"\n");
//            tvConsole.setText(stringBuilder.toString());
//        }
//    }

    /* zamień linijki w terminalu na jednego Stringa */
    private String linesListToString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(String line : consoleLinesList)
            stringBuilder.append(line+"\n");
        return stringBuilder.toString();
    }

    private class TerminalThread extends Thread{
        // TODO poprawić - występują puste lub ucięte odpowiedzi,
        private BluetoothConnection btConnection = BluetoothConnection.getInstance();
        private String command = "No command";
        private int nBytes;
        public TerminalThread(String command, int nBytes){
            this.command = command;
            //tymczasowo
            this.nBytes = 1;
            if(command.equalsIgnoreCase("010c"))
                this.nBytes = 2;
        }

        @Override
        public void run() {
            String response = "*BRAK ODPOWIEDZI*";
            try {
                synchronized (btConnection) {
                /* wyślij komendę do urządzenia */
                    btConnection.sendMsg(command);
                /* odczytaj odpowiedź */
                    response = btConnection.readMsg(nBytes);
                /* wyświetl odpowiedź */
                }

            } catch (Exception e) {
                Utils.saveLogInFile(logfileName, e, getApplicationContext());
            }

            Message msg = new Message();
            msg.obj = response;
            Bundle bundle = new Bundle();
            bundle.putString("RESPONSE", response);
            bundle.putString("COMMAND", command);
            msg.setData(bundle);
            msg.what = TERMINAL_DATA;
            handler.sendMessage(msg);
        }
    }
}
