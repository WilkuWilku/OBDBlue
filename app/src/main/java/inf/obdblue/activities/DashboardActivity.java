package inf.obdblue.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import inf.obdblue.BluetoothConnection;
import inf.obdblue.ParamsListAdapter;
import inf.obdblue.ParamsListItem;
import inf.obdblue.R;
import inf.obdblue.ReponseParser;
import inf.obdblue.commands.BasicCommands;

public class DashboardActivity extends AppCompatActivity {
    public static final String logFileName = "dashboard_logfile";
    private ArrayList<ParamsListItem> paramsListItems;
    private ParamsListAdapter paramsListAdapter;
    private ArrayList<BasicCommands> commandsToExecute;
    private Button bStart, bStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        commandsToExecute = new ArrayList<>();
        commandsToExecute.add(BasicCommands.ENGINE_RPM);
        commandsToExecute.add(BasicCommands.VEHICLE_SPEED);

        paramsListItems = new ArrayList<>();
        paramsListAdapter = new ParamsListAdapter(paramsListItems, this);
        ListView lvParams = (ListView) findViewById(R.id.paramsListView);
        lvParams.setAdapter(paramsListAdapter);

        for(int i=0; i<commandsToExecute.size(); i++)
            paramsListItems.add(new ParamsListItem(commandsToExecute.get(i).getDescription(), "NULL"));

        bStart = (Button) findViewById(R.id.startButton);
        bStop = (Button) findViewById(R.id.stopButton);
        bStop.setEnabled(false);
        final UpdatingTask updatingTask = new UpdatingTask();
        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bStart.setEnabled(false);
                bStop.setEnabled(true);
                updatingTask.execute(commandsToExecute);
            }
        });

        bStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bStop.setEnabled(false);
                bStart.setEnabled(true);
                updatingTask.cancel(true);
            }
        });
    }


    private class UpdatingTask extends AsyncTask<ArrayList<BasicCommands>, HashMap<BasicCommands, String>, Void> {
        private BluetoothConnection bluetoothConnection = BluetoothConnection.getInstance();

        @Override
        protected Void doInBackground(ArrayList<BasicCommands>... params) {
            HashMap<BasicCommands, String> valuesMap = new HashMap<>();
            ArrayList<BasicCommands> commandsList = params[0];
            String response;
            while(!isCancelled()) {
                for (BasicCommands command : commandsList) {
                    try {
                        bluetoothConnection.sendMsg(command.getCommand());
                        response = bluetoothConnection.readMsg();
                        int[] dataBytes = ReponseParser.parseToUnsignedBytesArray(response);
                        double value = command.convertResponse(dataBytes[0], dataBytes[1], dataBytes[2], dataBytes[3]);
                        valuesMap.put(command, value + " " + command.getUnits());
                        publishProgress(valuesMap);
                    } catch (IOException e) {
                        try {
                            OutputStream os = openFileOutput(logFileName, Context.MODE_PRIVATE);
                            os.write(e.getStackTrace().toString().getBytes());
                            os.close();
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(HashMap<BasicCommands, String>... values) {
            paramsListItems.clear();
            Set<BasicCommands> params = values[0].keySet();
            for(BasicCommands cmd : params)
                paramsListItems.add(new ParamsListItem(cmd.getDescription(), values[0].get(cmd)));
            paramsListAdapter.notifyDataSetChanged();
        }
    }

}
