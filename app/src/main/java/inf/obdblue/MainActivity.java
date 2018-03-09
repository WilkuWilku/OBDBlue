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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends FragmentActivity {

    private Button bFind, bToMenu;
    private BluetoothConnection bluetoothConn = BluetoothConnection.getInstance();
    private String status = "null";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bFind = (Button) findViewById(R.id.findButton);
        bToMenu = (Button) findViewById(R.id.menuButton);

        //status = getIntent().getStringExtra("STATUS");
        //updateStatusFragment();

        bFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //t.setText("Preparing to connect...");
                connect();
            }
        });

        bToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(getApplicationContext(), MenuActivity.class);
                //menuIntent.putExtra("STATUS", t.getText());
                startActivity(menuIntent);
            }
        });
    }

    private void connect(){
        try {
            boolean isFound = findBT();
            if(!isFound)
                return;
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            bluetoothConn.setBTSocket(bluetoothConn.getBTDevice().createRfcommSocketToServiceRecord(uuid));
            bluetoothConn.getBTSocket().connect();
            //t.setText("Connected with device: " + bluetoothConn.getBTDevice().getName());
            //updateStatusFragment();
            Toast.makeText(getApplicationContext(), "Connected with device successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean findBT(){
        bluetoothConn.setBTAdapter(BluetoothAdapter.getDefaultAdapter());
        if(bluetoothConn.getBTAdapter() == null) {
            //t.setText("No Bluetooth adapter available");
            Toast.makeText(getApplicationContext(), "Make sure your device is able to use Bluetooth", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!bluetoothConn.getBTAdapter().isEnabled()){
            final int REQUEST_RESPONSE = -420;
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_RESPONSE);
            if(REQUEST_RESPONSE == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Bluetooth connection is required", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(REQUEST_RESPONSE != RESULT_OK){
                Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        Set<BluetoothDevice> bondedDevices = bluetoothConn.getBTAdapter().getBondedDevices();
        if(bondedDevices.size() > 0) {
            bluetoothConn.setBTDevice(bondedDevices.iterator().next());
            //t.setText("Bluetooth device found");
        }
        return true;
    }

    private void updateStatusFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        StatusFragment statusFragment = (StatusFragment) fragmentManager.findFragmentById(R.id.statusFragment);
        fragmentManager.beginTransaction().detach(statusFragment).attach(statusFragment).commit();
    }
}
