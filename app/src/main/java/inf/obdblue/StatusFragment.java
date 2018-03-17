package inf.obdblue;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class StatusFragment extends Fragment {

    //private TextView tvStatus;
    private BluetoothConnection bluetoothConnection = BluetoothConnection.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateStatusText();
    }

    public void setStatusText(String text){
        TextView tvStatus = (TextView) getView().findViewById(R.id.statusTextView);
        tvStatus.setText(text);
    }

    public void setStatusText(int stringID){
        TextView tvStatus = (TextView) getView().findViewById(R.id.statusTextView);
        tvStatus.setText(stringID);
    }

    public void updateStatusText(){
        TextView tvStatus = (TextView) getView().findViewById(R.id.statusTextView);
        if(bluetoothConnection.getBTSocket() == null || !bluetoothConnection.getBTSocket().isConnected())
            tvStatus.setText(R.string.bluetooth_no_connection);
        else
            tvStatus.setText(R.string.bluetooth_connected+bluetoothConnection.getBTDevice().getName());
    }

}
