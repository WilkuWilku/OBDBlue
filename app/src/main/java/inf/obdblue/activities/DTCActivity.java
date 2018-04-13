package inf.obdblue.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import inf.obdblue.BluetoothConnection;
import inf.obdblue.R;

public class DTCActivity extends AppCompatActivity {

    private TextView tvRepair;
    private Button bRepair;
    private BluetoothConnection btConnection = BluetoothConnection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtc);


        tvRepair = (TextView) findViewById(R.id.repairTextView);
        bRepair = (Button) findViewById(R.id.repairButton);

        bRepair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    btConnection.sendMsg("04");
                    tvRepair.setText("Samochód śmiga jak należy");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
