package inf.obdblue;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Inf on 2018-03-14.
 */

/* Wątek aktualizujący wartości parametrów */

public class UpdatingTask extends AsyncTask<ArrayList<BasicCommands>, HashMap<BasicCommands, Double>, Void> {
    private HashMap<BasicCommands, Double> valuesMap = new HashMap<>();
    private BluetoothConnection bluetoothConnection = BluetoothConnection.getInstance();
    @Override
    protected Void doInBackground(ArrayList<BasicCommands>... params) {
        ArrayList<BasicCommands> commandsList = params[0];
        String response;
        for(BasicCommands command : BasicCommands.values()) {
            try {
                bluetoothConnection.sendMsg(command.getCommand());
                response = bluetoothConnection.readMsg();
                //TODO odczyt odpowiedzi i zapis do mapy
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(HashMap<BasicCommands, Double>... values) {
        //TODO aktualizacja komponentów UI
        super.onProgressUpdate(values);
    }
}
