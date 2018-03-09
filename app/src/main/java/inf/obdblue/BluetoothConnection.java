package inf.obdblue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Inf on 2018-03-03.
 */

public class BluetoothConnection {

    private BluetoothAdapter BTAdapter;
    private BluetoothDevice BTDevice;
    private BluetoothSocket BTSocket;
    private static OutputStream outputStream;
    private static InputStream inputStream;
    private static BluetoothConnection instance = null;

    private BluetoothConnection(){}

    public static BluetoothConnection getInstance(){
        if(instance == null)
            instance = new BluetoothConnection();
        return instance;
    }

    public static void sendMsg(String msg) throws IOException {
        outputStream.write((msg+"\r").getBytes());
        outputStream.flush();
    }

    public static String readMsg() throws IOException {
        final int BUFFER_SIZE = 1024;
        byte[] bytes = new byte[BUFFER_SIZE];
        inputStream.read(bytes);
        String msg = new String(bytes, "US-ASCII");
        return msg;
    }

    public void setBTAdapter(BluetoothAdapter BTAdapter) {
        this.BTAdapter = BTAdapter;
    }

    public void setBTDevice(BluetoothDevice BTDevice) {
        this.BTDevice = BTDevice;
    }

    public void setBTSocket(BluetoothSocket BTSocket) {
        this.BTSocket = BTSocket;
    }

    public BluetoothAdapter getBTAdapter() {
        return BTAdapter;
    }

    public BluetoothDevice getBTDevice() {
        return BTDevice;
    }

    public BluetoothSocket getBTSocket() {
        return BTSocket;
    }
}
