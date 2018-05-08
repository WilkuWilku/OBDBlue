package inf.obdblue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import inf.obdblue.BluetoothConnection;
import inf.obdblue.R;
import inf.obdblue.StatusFragment;



/* Aktywność z menu głównym */

public class MenuActivity extends AppCompatActivity {

    private Button bBTConnect, bDashboard, bTerminal, bSettings, bDTC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        bBTConnect = (Button) findViewById(R.id.bluetoothButton);
        bDashboard = (Button) findViewById(R.id.dashboardButton);
        bTerminal = (Button) findViewById(R.id.terminalButton);
        bSettings = (Button) findViewById(R.id.settingsButton);
        bDTC = (Button) findViewById(R.id.dtcButton);

        updateStatusFragment();

        bBTConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConnectionActivity.class);
                startActivity(intent);
            }
        });

        bDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
            }
        });

        bTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TerminalActivity.class);
                startActivity(intent);
            }
        });

        bSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        bDTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DTCActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusFragment();
    }

    private void updateStatusFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        final StatusFragment statusFragment = (StatusFragment) fragmentManager.findFragmentById(R.id.fragment);
        statusFragment.updateStatusText();
    }

    @Override
    protected void onDestroy() {
        try {
            BluetoothConnection.getInstance().closeAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
