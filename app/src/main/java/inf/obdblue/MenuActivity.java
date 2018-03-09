package inf.obdblue;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Objects;

public class MenuActivity extends AppCompatActivity {

    private BluetoothConnection bluetoothConnection = BluetoothConnection.getInstance();
    private BluetoothAdapter BTAdapter = bluetoothConnection.getBTAdapter();
    private BluetoothDevice BTDevice = bluetoothConnection.getBTDevice();
    private BluetoothSocket BTSocket = bluetoothConnection.getBTSocket();
    private Button bBTConnect;
    //private StatusFragment statusFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //statusFragment = (StatusFragment) getSupportFragmentManager().findFragmentById(R.id.statusFragment);
        bBTConnect = (Button) findViewById(R.id.bluetoothButton);

        Intent intent = getIntent();
        //String btStatus = intent.getExtras().getString("STATUS");
        //if(btStatus == null)
        //    statusFragment.setStatusText("No device connected");
        //else
        //    statusFragment.setStatusText(btStatus);


        bBTConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent btConnIntent = new Intent(getApplicationContext(), MainActivity.class);
                //btConnIntent.putExtra("STATUS", statusFragment.getStatusText());
                startActivity(btConnIntent);
            }
        });

    }

}
