package inf.obdblue.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import inf.obdblue.BluetoothConnection;
import inf.obdblue.R;
import inf.obdblue.StatusFragment;
import inf.obdblue.Utils;


/* Aktywność do łączenia się z urządzeniem */

public class ConnectionActivity extends FragmentActivity {
    private Activity instance = this;
    public static final String logfileName = "btConnLogfile";
    private static final String TAG = "CONNECTION_ACTIVITY";
    private Button bFind, bDisconnect;
    private BluetoothConnection bluetoothConn = BluetoothConnection.getInstance();
    private StatusFragment statusFragment;
    private int checkedDevicePosition = 0;
    private ListView lvDevices;
    private ArrayList<BluetoothDevice> devicesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        lvDevices = (ListView) findViewById(R.id.deviceList);
        bFind = (Button) findViewById(R.id.findButton);
        bDisconnect = (Button) findViewById(R.id.disconnectButton);
        bDisconnect.setEnabled(false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        statusFragment = (StatusFragment) fragmentManager.findFragmentById(R.id.fragment);
        bFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusFragment.setStatusText(R.string.bluetooth_searching_devices);
                devicesList = bluetoothConn.searchBTDevices(instance, lvDevices, statusFragment);
                //if(devices != null && devices.size() > 0)
                    //selectDeviceAndConnect(devices);
            }
        });
        bDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* zamknij wszystkie strumienie i gniazdo */
                try {
                    bluetoothConn.closeAll();
                    bluetoothConn.getBTSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /* włącz wyszukiwanie nowych urządzeń, wyłącz rozłączanie */
                bFind.setEnabled(true);
                bDisconnect.setEnabled(false);
            }
        });
        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothConn.getBTAdapter().cancelDiscovery();
                BluetoothDevice device = devicesList.get(position);
                connectWithDevice(device);
            }
        });

    }

    /* lista Stringów z adresami MAC powiązanych urządzeń */
    private final String[] getDevicesNames(ArrayList<BluetoothDevice> devices){
        String[] devicesNames = new String[devices.size()];
        for(int i=0; i<devices.size(); i++)
            devicesNames[i] = devices.get(i).getName() + " (MAC: "+devices.get(i).getAddress()+")";
        return devicesNames;
    }


    private void selectDeviceAndConnect(final ArrayList<BluetoothDevice> deviceList){
        AlertDialog dialog;
        /* Dialog z listą powiązanych urządzeń */
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setSingleChoiceItems(getDevicesNames(deviceList), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setTitle(R.string.device_dialog_title)
                .setNegativeButton(R.string.device_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        statusFragment.setStatusText(R.string.bluetooth_no_connection);
                        dialog.cancel();
                    }
                })
                .setNeutralButton(R.string.device_dialog_connect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedDevicePosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        bluetoothConn.setBTDevice(deviceList.get(checkedDevicePosition));
                        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                        statusFragment.setStatusText(R.string.bluetooth_establishing_connection);
                        try {
                            /* utwórz gniazdo */
                            bluetoothConn.setBTSocket(bluetoothConn.getBTDevice().createRfcommSocketToServiceRecord(uuid));
                            bluetoothConn.getBTSocket().connect();
                            /* odśwież status */
                            statusFragment.updateStatusText();
                            Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_connected_successfully_toast)+" "+bluetoothConn.getBTDevice().getName()+"!", Toast.LENGTH_SHORT).show();
                            /* włącz rozłączanie, wyłącz wyszukiwanie urządzeń */
                            bDisconnect.setEnabled(true);
                            bFind.setEnabled(false);
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), R.string.bluetooth_connection_error, Toast.LENGTH_SHORT).show();
                            statusFragment.updateStatusText();
                            /* zapisz błąd w pliku */
                            Utils.saveLogInFile(logfileName, e, getApplicationContext());
                        }
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    private void connectWithDevice(BluetoothDevice device) {
        bluetoothConn.setBTDevice(device);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        statusFragment.setStatusText(R.string.bluetooth_establishing_connection);
        try {
            /* utwórz gniazdo */
            bluetoothConn.setBTSocket(bluetoothConn.getBTDevice().createRfcommSocketToServiceRecord(uuid));
            bluetoothConn.getBTSocket().connect();
            /* odśwież status */
            statusFragment.updateStatusText();
            Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_connected_successfully_toast) + " " + bluetoothConn.getBTDevice().getName(), Toast.LENGTH_SHORT).show();
            /* włącz rozłączanie, wyłącz wyszukiwanie urządzeń */
            bDisconnect.setEnabled(true);
            bFind.setEnabled(false);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), R.string.bluetooth_connection_error, Toast.LENGTH_SHORT).show();
            statusFragment.updateStatusText();
            /* zapisz błąd w pliku */
            Utils.saveLogInFile(logfileName, e, getApplicationContext());
        }
    }

    @Override
    public void onBackPressed() {
        BluetoothAdapter adapter = bluetoothConn.getBTAdapter();
        if(adapter != null && adapter.isDiscovering())
            bluetoothConn.getBTAdapter().cancelDiscovery();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(bluetoothConn.getBroadcastReceiver());
        } catch (Exception e) {}
        super.onDestroy();
    }
}
