package inf.obdblue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/* Aktywność z menu głównym */

public class MenuActivity extends AppCompatActivity {

    private Button bBTConnect;
    private Button bDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        bBTConnect = (Button) findViewById(R.id.bluetoothButton);
        bDashboard = (Button) findViewById(R.id.dashboardButton);

        FragmentManager fragmentManager = getSupportFragmentManager();
        final StatusFragment statusFragment = (StatusFragment) fragmentManager.findFragmentById(R.id.fragment);
        statusFragment.updateStatusText();

        bBTConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent btConnIntent = new Intent(getApplicationContext(), ConnectionActivity.class);
                startActivity(btConnIntent);
            }
        });

        bDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboardIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(dashboardIntent);
            }
        });

    }

}
