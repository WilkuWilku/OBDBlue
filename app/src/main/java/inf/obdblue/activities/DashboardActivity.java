package inf.obdblue.activities;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import inf.obdblue.BluetoothConnection;
import inf.obdblue.ParamsListAdapter;
import inf.obdblue.ParamsListItem;
import inf.obdblue.R;
import inf.obdblue.ReponseParser;
import inf.obdblue.Utils;
import inf.obdblue.commands.BasicCommands;

public class DashboardActivity extends AppCompatActivity {
    private static final int DASHBOARD_DATA = 420;
    public static final String logfileName = "dashboard_logfile";
    private ArrayList<ParamsListItem> paramsListItems;
    private ParamsListAdapter paramsListAdapter;
    private ArrayList<BasicCommands> commandsToExecute;
    private Button bStart, bStop;
    private Handler handler;
    private DashboardThread dashboardThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        /* dodaj wszystkie komendy, jakie będą wykonywane */
        commandsToExecute = new ArrayList<>();
        commandsToExecute.add(BasicCommands.ENGINE_RPM);
        commandsToExecute.add(BasicCommands.VEHICLE_SPEED);

        /* lista do wyświetlania wyników */
        paramsListItems = new ArrayList<>();
        paramsListAdapter = new ParamsListAdapter(paramsListItems, this);
        final ListView lvParams = (ListView) findViewById(R.id.paramsListView);
        lvParams.setAdapter(paramsListAdapter);

        /* dodaj pozycje do listy wyników */
        for(int i=0; i<commandsToExecute.size(); i++)
            paramsListItems.add(new ParamsListItem(commandsToExecute.get(i).getDescription(), "NULL"));

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case DASHBOARD_DATA:
                        paramsListItems = new ArrayList<>();
                        HashMap<BasicCommands, String> cmdMap = ((HashMap<BasicCommands, String>)(msg.obj));
                        Set<BasicCommands> params = cmdMap.keySet();
                        for(BasicCommands cmd : params)
                            paramsListItems.add(new ParamsListItem(cmd.getDescription(), cmdMap.get(cmd)));
                        lvParams.setAdapter(new ParamsListAdapter(paramsListItems, getApplicationContext()));
                        paramsListAdapter.notifyDataSetChanged();
                        break;
                    default: super.handleMessage(msg); break;
                }
            }
        };

        bStart = (Button) findViewById(R.id.startButton);
        bStop = (Button) findViewById(R.id.stopButton);
        bStop.setEnabled(false);



        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final UpdatingTask updatingTask = new UpdatingTask();
                dashboardThread = new DashboardThread(commandsToExecute);
                dashboardThread.start();
                bStart.setEnabled(false);
                bStop.setEnabled(true);
                //updatingTask.execute(commandsToExecute);
            }
        });

        bStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboardThread.finish();
                bStop.setEnabled(false);
                bStart.setEnabled(true);
                //updatingTask.cancel(true);
            }
        });
    }


//    private class UpdatingTask extends AsyncTask<ArrayList<BasicCommands>, HashMap<BasicCommands, String>, Void> {
//        private BluetoothConnection bluetoothConnection = BluetoothConnection.getInstance();
//
//        @Override
//        protected Void doInBackground(ArrayList<BasicCommands>... params) {
//            HashMap<BasicCommands, String> valuesMap = new HashMap<>();
//            ArrayList<BasicCommands> commandsList = params[0];
//            String response;
//            //while(!isCancelled()) {
//                for (BasicCommands command : commandsList) {
//                    try {
//                        /* wyślij komendę do urządzenia */
//                        bluetoothConnection.sendMsg(command.getCommand());
//                        /* odczytaj odpowiedź */
//                        response = bluetoothConnection.readMsg();
//                        /* parsuj odpowiedź na bajty A, B, C, D */
//                        int[] dataBytes = ReponseParser.parseToUnsignedBytesArray(response);
//                        /* przekonwertuj na ostateczny wynik */
//                        double value = command.convertResponse(dataBytes[0], dataBytes[1], dataBytes[2], dataBytes[3]);
//                        /* dodaj do mapy wyników */
//                        valuesMap.put(command, value + " " + command.getUnits());
//                        publishProgress(valuesMap);
//                    } catch (Exception e){
//                        /* zapisz błąd w pliku */
//                       Utils.saveLogInFile(logfileName, e, getApplicationContext());
//                    }
//                }
//            //}
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(HashMap<BasicCommands, String>... values) {
//            paramsListItems.clear();
//            Set<BasicCommands> params = values[0].keySet();
//            for(BasicCommands cmd : params)
//                paramsListItems.add(new ParamsListItem(cmd.getDescription(), values[0].get(cmd)));
//            paramsListAdapter.notifyDataSetChanged();
//        }
//    }

    private class DashboardThread extends Thread{
        // TODO poprawić - zmienna częstotliwość odświeżania, prędkość czasami nieprawidłowa
        private BluetoothConnection bluetoothConnection = BluetoothConnection.getInstance();
        private ArrayList<BasicCommands> commandsList;
        private HashMap<BasicCommands, String> valuesMap = new HashMap<>();
        private volatile boolean isRunning = false;

        public DashboardThread(ArrayList<BasicCommands> commandsList){
            this.commandsList = commandsList;
        }

        public void finish(){
            isRunning = false;
        }

        @Override
        public void run() {
            String response;
            isRunning = true;
            while (isRunning) {
                for (BasicCommands command : commandsList) {
                    try {
                        synchronized (bluetoothConnection) {
                        /* wyślij komendę do urządzenia */
                            bluetoothConnection.sendMsg(command.getCommand());
                        /* odczytaj odpowiedź */
                            response = bluetoothConnection.readMsg(command.getnDataBytes());
                        }
                        /* parsuj odpowiedź na bajty A, B, C, D */
                        int[] dataBytes = ReponseParser.parseToUnsignedBytesArray(response, command.getnDataBytes());
                        /* przekonwertuj na ostateczny wynik */
                        double value = command.convertResponse(dataBytes[0], dataBytes[1], dataBytes[2], dataBytes[3]);
                        /* dodaj do mapy wyników */
                        valuesMap.put(command, value + " " + command.getUnits());

                    } catch (Exception e) {
                        /* zapisz błąd w pliku */
                        Utils.saveLogInFile(logfileName, e, getApplicationContext());
                    }
                }
                Message msg = new Message();
                msg.obj = valuesMap;
                msg.what = DASHBOARD_DATA;
                handler.sendMessage(msg);
            }
        }
    }

}
