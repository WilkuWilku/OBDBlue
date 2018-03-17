package inf.obdblue.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import inf.obdblue.BluetoothConnection;
import inf.obdblue.R;
import inf.obdblue.StatusFragment;


/* Aktywność do łączenia się z urządzeniem */

public class ConnectionActivity extends FragmentActivity {
    private static final String TAG = "CONNECTION_ACTIVITY";
    private static final int BT_REQUEST_CODE = 1;
    private Button bFind;
    private BluetoothConnection bluetoothConn = BluetoothConnection.getInstance();
    private StatusFragment statusFragment;
    private int checkedDevicePosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        bFind = (Button) findViewById(R.id.findButton);

        FragmentManager fragmentManager = getSupportFragmentManager();
        statusFragment = (StatusFragment) fragmentManager.findFragmentById(R.id.fragment);

        bFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusFragment.setStatusText(R.string.bluetooth_preparing_to_connect);
                Log.i(TAG, "pre selectDeviceAndConnect");
                findBT();
            }
        });

    }

    private void findBT(){
        Log.i(TAG, "FindBT started");
        bluetoothConn.setBTAdapter(BluetoothAdapter.getDefaultAdapter());
        if(bluetoothConn.getBTAdapter() == null) {
            Toast.makeText(getApplicationContext(), R.string.bluetooth_no_adapter_toast, Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothConn.getBTAdapter().isEnabled()){
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, BT_REQUEST_CODE);
            return;
        }

        statusFragment.setStatusText(R.string.bluetooth_searching_bonded_devices);
        Set<BluetoothDevice> bondedDevices = bluetoothConn.getBTAdapter().getBondedDevices();
        ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
        deviceList.addAll(bondedDevices);
        if(bondedDevices.size() > 0) {
            statusFragment.setStatusText(R.string.bluetooth_bonded_device_found);
            Log.i(TAG, "pre dialog");
            selectDeviceAndConnect(deviceList);
        }
        else {
            statusFragment.setStatusText(R.string.bluetooth_no_connection);
            Toast.makeText(getApplicationContext(), R.string.bluetooth_no_bonded_devices_found, Toast.LENGTH_SHORT).show();
        }
    }

    private String[] getDevicesNames(ArrayList<BluetoothDevice> devices){
        String[] devicesNames = new String[devices.size()];
        for(int i=0; i<devices.size(); i++)
            devicesNames[i] = devices.get(i).getName();
        return devicesNames;
    }

    private void selectDeviceAndConnect(final ArrayList<BluetoothDevice> deviceList){
        AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.device_dialog_message)
                .setCancelable(true)
                .setSingleChoiceItems(getDevicesNames(deviceList), checkedDevicePosition, null)
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
                        Log.i(TAG, "just after dialog");
                        bluetoothConn.setBTDevice(deviceList.get(checkedDevicePosition));
                        Log.i(TAG, "device is set");
                        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                        statusFragment.setStatusText(R.string.bluetooth_establishing_connection);
                        Log.i(TAG, "pre socket");
                        try {
                            bluetoothConn.setBTSocket(bluetoothConn.getBTDevice().createRfcommSocketToServiceRecord(uuid));
                            Log.i(TAG, "after socket");
                            bluetoothConn.getBTSocket().connect();
                            statusFragment.updateStatusText();
                            Toast.makeText(getApplicationContext(), R.string.bluetooth_connected_successfully_toast+" "+bluetoothConn.getBTDevice().getName()+"!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                        e.printStackTrace();
                    }
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }


}
