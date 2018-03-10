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
import java.util.Set;
import java.util.UUID;

public class MainActivity extends FragmentActivity {

    private Button bFind, bToMenu;
    private BluetoothConnection bluetoothConn = BluetoothConnection.getInstance();
    private String status = "null";
    private StatusFragment statusFragment;
    private static final int BT_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bFind = (Button) findViewById(R.id.findButton);
        bToMenu = (Button) findViewById(R.id.menuButton);


        FragmentManager fragmentManager = getSupportFragmentManager();
        statusFragment = (StatusFragment) fragmentManager.findFragmentById(R.id.statusFragment);
        if(savedInstanceState == null) {
            statusFragment = new StatusFragment();
            fragmentManager.beginTransaction().add(R.id.fragment, statusFragment).commit();
//            statusFragment.updateStatusText();
        }
        //status = getIntent().getStringExtra("STATUS");


        bFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusFragment statusFrag = (StatusFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                statusFrag.setStatusText("Preparing to connect...");
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

        Set<BluetoothDevice> bondedDevices = bluetoothConn.getBTAdapter().getBondedDevices();
        if(bondedDevices.size() > 0) {
            bluetoothConn.setBTDevice(bondedDevices.iterator().next());
            statusFragment.setStatusText("Bluetooth device found");
        }
        return true;
    }

}
