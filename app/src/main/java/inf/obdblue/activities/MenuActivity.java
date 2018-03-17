package inf.obdblue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import inf.obdblue.R;
import inf.obdblue.StatusFragment;



/* Aktywność z menu głównym */

public class MenuActivity extends AppCompatActivity {

    private Button bBTConnect, bDashboard, bTerminal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        bBTConnect = (Button) findViewById(R.id.bluetoothButton);
        bDashboard = (Button) findViewById(R.id.dashboardButton);
        bTerminal = (Button) findViewById(R.id.terminalButton);

        updateStatusFragment();

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

        bTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent terminalIntent = new Intent(getApplicationContext(), TerminalActivity.class);
                startActivity(terminalIntent);
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
}
