package inf.obdblue.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import inf.obdblue.R;


public class TerminalActivity extends AppCompatActivity {

    private Button bSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        bSend = (Button) findViewById(R.id.sendCommandButton);
    }
}
