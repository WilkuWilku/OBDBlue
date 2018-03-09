package inf.obdblue;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class StatusFragment extends Fragment {

    private TextView tvStatus;
    private BluetoothConnection bluetoothConnection = BluetoothConnection.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvStatus = (TextView) view.findViewById(R.id.statusTextView);
        if(bluetoothConnection.getBTDevice() == null)
            tvStatus.setText("No connection");
        else
            tvStatus.setText("Connected with "+bluetoothConnection.getBTDevice().getName());
    }


}
