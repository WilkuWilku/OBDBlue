package inf.obdblue;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends FragmentActivity {

    private static final int BT_REQUEST_CODE = 1;
    private Button bFind;
    private BluetoothConnection bluetoothConn = BluetoothConnection.getInstance();
    private StatusFragment statusFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bFind = (Button) findViewById(R.id.findButton);


        FragmentManager fragmentManager = getSupportFragmentManager();
        statusFragment = (StatusFragment) fragmentManager.findFragmentById(R.id.fragment);

        bFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusFragment.setStatusText("Preparing to connect...");
                connect();
            }
        });

    }

    private void connect(){
        try {
            boolean isFound = findBT();
            if(!isFound) {
                statusFragment.setStatusText("No connection");
                return;
            }
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            statusFragment.setStatusText("Establishing connection...");
            bluetoothConn.setBTSocket(bluetoothConn.getBTDevice().createRfcommSocketToServiceRecord(uuid));
            bluetoothConn.getBTSocket().connect();

            statusFragment.updateStatusText();
            Toast.makeText(getApplicationContext(), "Connected with device successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean findBT(){
        bluetoothConn.setBTAdapter(BluetoothAdapter.getDefaultAdapter());
        if(bluetoothConn.getBTAdapter() == null) {
            statusFragment.setStatusText("No Bluetooth adapter available");
            Toast.makeText(getApplicationContext(), "Make sure your device is able to use Bluetooth", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!bluetoothConn.getBTAdapter().isEnabled()){
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, BT_REQUEST_CODE);
        }
        if(!bluetoothConn.getBTAdapter().isEnabled())
            return false;
        statusFragment.setStatusText("Searching for bonded devices...");
        Set<BluetoothDevice> bondedDevices = bluetoothConn.getBTAdapter().getBondedDevices();
        ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
        deviceList.addAll(bondedDevices);
        if(bondedDevices.size() > 0) {
            bluetoothConn.setBTDevice(deviceList.get(0));
            statusFragment.setStatusText("Bluetooth device found");
        }
        return true;
    }

}
