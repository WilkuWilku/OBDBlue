package inf.obdblue.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import inf.obdblue.BluetoothConnection;
import inf.obdblue.R;
import inf.obdblue.StatusFragment;


/* Aktywność do łączenia się z urządzeniem */

public class ConnectionActivity extends FragmentActivity {
    public static final String logFileName = "btConnLogfile";
    private static final String TAG = "CONNECTION_ACTIVITY";
    private static final int BT_REQUEST_CODE = 1;
    private Button bFind, bDisconnect;
    private BluetoothConnection bluetoothConn = BluetoothConnection.getInstance();
    private StatusFragment statusFragment;
    private int checkedDevicePosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        bFind = (Button) findViewById(R.id.findButton);
        bDisconnect = (Button) findViewById(R.id.disconnectButton);
        bDisconnect.setEnabled(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        statusFragment = (StatusFragment) fragmentManager.findFragmentById(R.id.fragment);
        bFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusFragment.setStatusText(R.string.bluetooth_preparing_to_connect);
                findBT();
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

    }

    private void findBT(){
        bluetoothConn.setBTAdapter(BluetoothAdapter.getDefaultAdapter());
        /* sprawdź, czy urządzenie obsługuje Bluetooth */
        if(bluetoothConn.getBTAdapter() == null) {
            Toast.makeText(getApplicationContext(), R.string.bluetooth_no_adapter_toast, Toast.LENGTH_SHORT).show();
        }
        /* włącz Bluetooth, jeśli jest wyłączone */
        if(!bluetoothConn.getBTAdapter().isEnabled()){
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, BT_REQUEST_CODE);
            return;
        }
        /* wyszukaj sparowane urządzenia */
        statusFragment.setStatusText(R.string.bluetooth_searching_bonded_devices);
        Set<BluetoothDevice> bondedDevices = bluetoothConn.getBTAdapter().getBondedDevices();
        ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
        deviceList.addAll(bondedDevices);
        if(bondedDevices.size() > 0) {
            statusFragment.setStatusText(R.string.bluetooth_bonded_device_found);
            selectDeviceAndConnect(deviceList);
        }
        else {
            statusFragment.setStatusText(R.string.bluetooth_no_connection);
            Toast.makeText(getApplicationContext(), R.string.bluetooth_no_bonded_devices_found, Toast.LENGTH_SHORT).show();
        }
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
                            try {
                                File file = new File(getFilesDir(), logFileName);
                                if(file == null)
                                    file.createNewFile();
                                OutputStream os = openFileOutput(logFileName, Context.MODE_PRIVATE);
                                os.write(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()).getBytes());
                                os.write(("\n"+e.getMessage()+"\n").getBytes());
                                for(StackTraceElement stackTraceElement : e.getStackTrace()) {
                                    os.write((stackTraceElement.toString()+"\n").getBytes());
                                }
                                os.close();
                            } catch (FileNotFoundException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                    }
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }
}
