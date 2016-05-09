package ir.doorbash.gamepad;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {


    public static final String ACTION_USB_PERMISSION = "ir.doorbash.gamepad.usb";
    private static final String TAG = "RemoteGamepad";


    UsbManager usbManager;
    List<ThreadUSB> threads = new ArrayList<>();
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d(TAG, "hello there!");


        initSocket("http://192.168.1.16:3000");

        try {
            getPermissionForUsbDevices();
        } catch (Exception e) {

        }

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        threads.clear();

        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();

            int vendor = device.getVendorId();
            int product = device.getProductId();

            if (GamePadUtil.isGamepadSupported(vendor, product)) {
                try {
                    ThreadUSB thread = new ThreadUSB(this, device, threads.size(), socket);
                    threads.add(thread);
                    thread.start();
                } catch (Exception eee) {
                }
            }
        }
    }

    public enum GamePadType {
        NORMAL, V121P6, V2064P1, V2079P58369, V4797P40993
    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Toast.makeText(MainActivity.this, "Gamepad connected!", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                    }
                }
            }
        }
    };


    public void getPermissionForUsbDevices() {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        try {
            registerReceiver(mUsbReceiver, filter);
        } catch (Exception e) {
        }

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();

            if (GamePadUtil.isGamepadSupported(device.getVendorId(), device.getProductId())) {
                manager.requestPermission(device, mPermissionIntent);
            }

        }
    }

    private void initSocket(String url) {
        try {
            socket = IO.socket(url);
            socket.io().reconnection(true);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Connected to server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Connection closed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, args.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Connection error.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            socket.connect();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }


    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mUsbReceiver);
        } catch (Exception e) {
        }
        try {
            socket.close();
        } catch (Exception e) {

        }
        try {

            if (threads != null) {
                for (ThreadUSB thread : threads) {
                    thread.kill = true;
                }
                threads.clear();
            }
        } catch (Exception e) {

        }
        super.onDestroy();
    }


}
