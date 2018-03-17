package inf.obdblue.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import inf.obdblue.ParamsListAdapter;
import inf.obdblue.ParamsListItem;
import inf.obdblue.R;
import inf.obdblue.commands.BasicCommands;

public class DashboardActivity extends AppCompatActivity {
    private ArrayList<ParamsListItem> paramsListItems;
    private ParamsListAdapter paramsListAdapter;
    private Button bStart, bStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        paramsListItems = new ArrayList<>();
        paramsListAdapter = new ParamsListAdapter(paramsListItems, this);
        ListView lvParams = (ListView) findViewById(R.id.paramsListView);
        lvParams.setAdapter(paramsListAdapter);
        paramsListItems.add(new ParamsListItem(BasicCommands.ENGINE_RPM.getDescription(), "50"));
        paramsListItems.add(new ParamsListItem(BasicCommands.VEHICLE_SPEED.getDescription(), "150"));

        bStart = (Button) findViewById(R.id.startButton);
        bStop = (Button) findViewById(R.id.stopButton);
        bStop.setEnabled(false);

        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bStart.setEnabled(false);
                bStop.setEnabled(true);
                //TODO uruchomić wątek aktualizujący
            }
        });

        bStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bStop.setEnabled(false);
                bStart.setEnabled(true);
                //TODO zatrzymać wątek aktualizujący
            }
        });
    }
}
