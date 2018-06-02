package inf.obdblue;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Inf on 2018-03-03.
 */

/* Klasa-singleton obsługująca połączenie z urządzeniem poprzez Bluetooth */

public class BluetoothConnection {
    private static final int BT_REQUEST_CODE = 1;
    private final int BUFFER_SIZE = 128;
    private BluetoothAdapter BTAdapter;
    private BluetoothDevice BTDevice;
    private BluetoothSocket BTSocket;
    private static OutputStream outputStream;
    private static InputStream inputStream;
    private static BluetoothConnection instance = null;
    private BroadcastReceiver broadcastReceiver;
    private ArrayList<BluetoothDevice> BTDevicesList = new ArrayList<>();

    private BluetoothConnection(){}

    public static BluetoothConnection getInstance(){
        if(instance == null)
            instance = new BluetoothConnection();
        return instance;
    }

    public void sendMsg(String msg) throws IOException {
        outputStream = BTSocket.getOutputStream();
        outputStream.write((msg+"\r").getBytes());
        outputStream.flush();
    }

    public String readMsg(int nDataBytes) throws IOException {
        // TODO wczytywanie całej odpowiedzi
        byte[] bytes = new byte[BUFFER_SIZE];
        inputStream = BTSocket.getInputStream();
        int nRead = inputStream.read(bytes);
        String msg = new String(bytes, 0, nRead, "US-ASCII");
        return msg;
    }

    public void setBTAdapter(BluetoothAdapter BTAdapter) {
        this.BTAdapter = BTAdapter;
    }

    public void setBTDevice(BluetoothDevice BTDevice) {
        this.BTDevice = BTDevice;
    }

    public void setBTSocket(BluetoothSocket BTSocket) {
        this.BTSocket = BTSocket;
    }

    public BluetoothAdapter getBTAdapter() {
        return BTAdapter;
    }

    public BluetoothDevice getBTDevice() {
        return BTDevice;
    }

    public BluetoothSocket getBTSocket() {
        return BTSocket;
    }

    public BroadcastReceiver getBroadcastReceiver() {
        return broadcastReceiver;
    }

//    public ArrayList<BluetoothDevice> findBT(Activity activity, StatusFragment statusFragment){
//        setBTAdapter(BluetoothAdapter.getDefaultAdapter());
//        /* sprawdź, czy urządzenie obsługuje Bluetooth */
//        if(getBTAdapter() == null) {
//            Toast.makeText(activity.getApplicationContext(), R.string.bluetooth_no_adapter_toast, Toast.LENGTH_SHORT).show();
//        }
//        /* włącz Bluetooth, jeśli jest wyłączone */
//        if(!getBTAdapter().isEnabled()){
//            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            activity.startActivityForResult(enableBT, BT_REQUEST_CODE);
//            return null;
//        }
//        /* wyszukaj sparowane urządzenia */
//        statusFragment.setStatusText(R.string.bluetooth_searching_bonded_devices);
//        Set<BluetoothDevice> bondedDevices = getBTAdapter().getBondedDevices();
//        ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
//        deviceList.addAll(bondedDevices);
//        if(bondedDevices.size() > 0) {
//            statusFragment.setStatusText(R.string.bluetooth_bonded_device_found);
//        }
//        else {
//            statusFragment.setStatusText(R.string.bluetooth_no_connection);
//            Toast.makeText(activity.getApplicationContext(), R.string.bluetooth_no_bonded_devices_found, Toast.LENGTH_SHORT).show();
//        }
//        return deviceList;
//    }


    public ArrayList<BluetoothDevice> searchBTDevices(Activity activity, ListView deviceListView, StatusFragment statusFragment){
        setBTAdapter(BluetoothAdapter.getDefaultAdapter());
        /* sprawdź, czy urządzenie obsługuje Bluetooth */
        if(getBTAdapter() == null) {
            Toast.makeText(activity.getApplicationContext(), R.string.bluetooth_no_adapter_toast, Toast.LENGTH_SHORT).show();
        }
        /* włącz Bluetooth, jeśli jest wyłączone */
        if(!getBTAdapter().isEnabled()){
            Intent enableBTintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBTintent, BT_REQUEST_CODE);
            return null;
        }
        ArrayList<String> devicesTextList = new ArrayList<>();
        BTDevicesList = new ArrayList<>();
        /* wyszukaj dostępne urządzenia */
        BroadcastReceiver bReceiver = prepareBroadcastReceiver(BTDevicesList, devicesTextList, deviceListView, statusFragment);
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(bReceiver, intentFilter);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        if(BTAdapter.isDiscovering())
            BTAdapter.cancelDiscovery();
        BTAdapter.startDiscovery();
        return BTDevicesList;
    }

    private BroadcastReceiver prepareBroadcastReceiver(final ArrayList<BluetoothDevice> devicesList, final ArrayList<String> devicesTextList, final ListView deviceListView, final StatusFragment statusFragment){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devicesList.add(device);
                    devicesTextList.add(device.getName() + "\nMAC: "+device.getAddress());
                    deviceListView.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, devicesTextList));
                }
                if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    statusFragment.setStatusText(R.string.bluetooth_searching_finished);
                }
            }
        };
        return broadcastReceiver;
    }

    public void closeAll() throws IOException{
        if (outputStream != null)
            outputStream.close();
        if (inputStream != null)
            inputStream.close();
        if (BTSocket != null)
            BTSocket.close();
    }

}
