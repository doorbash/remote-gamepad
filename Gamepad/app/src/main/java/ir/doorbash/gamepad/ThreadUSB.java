package ir.doorbash.gamepad;

import android.annotation.TargetApi;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.Build;
import android.widget.Toast;

import io.socket.client.Socket;

/**
 * Created by Milad Doorbash on 5/9/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
class ThreadUSB extends Thread {
    private MainActivity activity;
    public boolean kill = false;
    UsbDevice device;
    UsbDeviceConnection connection;
    UsbEndpoint endpoint;
    int id;
    int device_id = 0;
    byte[] buffer = new byte[8];
    MainActivity.GamePadType gamePadType;
    Socket socket;

    public ThreadUSB(MainActivity activity, UsbDevice device, int id, Socket socket) {
        this.activity = activity;
        this.device = device;
        this.id = id;
        if (device.getProductId() == 6) {
            gamePadType = MainActivity.GamePadType.V121P6;
        } else if (device.getProductId() == 1) {
            gamePadType = MainActivity.GamePadType.V2064P1;
        } else if (device.getProductId() == 58369) {
            gamePadType = MainActivity.GamePadType.V2079P58369;
        } else if (device.getProductId() == 40993) {
            gamePadType = MainActivity.GamePadType.V4797P40993;
        } else {
            gamePadType = MainActivity.GamePadType.NORMAL;
        }
        this.socket = socket;
    }

    @Override
    public void run() {

        try {

            try {
                connection = activity.usbManager.openDevice(device);
            } catch (Exception e) {
                return;
            }

            device_id = 0;


            UsbInterface intf = device.getInterface(0);
            endpoint = intf.getEndpoint(0);

            while (!connection.claimInterface(intf, true)) {

            }

            int pad = -1;
            int old_pad = 0;

            while (true) {

                if (kill) return;


                if (connection.bulkTransfer(endpoint, buffer, 8, 200) < 0) {
                    break;
                } else {

                    device_id = id;

                    old_pad = pad;


                    switch (gamePadType) {
                        case NORMAL:
                            device_id += (buffer[0] - 1);
                            pad = readKeys();
                            break;
                        case V121P6:
                            device_id += 0;
                            pad = readV121P6();
                            break;
                        case V2064P1:
                            device_id += (buffer[0] - 1);
                            pad = readV2064P1();
                            break;
                        case V2079P58369:
                            device_id += 0;
                            pad = readV2079P58369();
                            break;
                        case V4797P40993:
                            device_id += 0;
                            pad = read4797P40993();
                            break;
                    }
                }
                if(old_pad != pad)
                    socket.emit("e",id + " " + pad);
            }


            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Device disconnected.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception eee) {
            try {
                connection.close();
            } catch (Exception eeee) {

            }


            eee.printStackTrace();
        }

    }

    private int readKeys() {
        int pad = 0;

        if ((buffer[5] & 0x4F) == 0x4F) {
            pad |= InputConfig.getButton(0);
            // Log.d("JOYRAW", device_id + " : " + "B");
        }

        if ((buffer[5] & 0x8F) == 0x8F) {
            pad |= InputConfig.getButton(1);
            // Log.d("JOYRAW", device_id + " : " + "A");
        }

        if ((buffer[6] & 0x10) == 0x10) {
            pad |= InputConfig.getButton(2);
            // Log.d("JOYRAW", device_id + " : " + "MODE");
        }

        if ((buffer[6] & 0x20) == 0x20) {
            pad |= InputConfig.getButton(3);
            // Log.d("JOYRAW", device_id + " : " + "START");
        }

        if ((buffer[4] & 0x7F) == 0) {
            pad |= InputConfig.getButton(4);
            // Log.d("JOYRAW", device_id + " : " + "up");
        }

        if ((buffer[4] & 0x80) == 0x80) {
            pad |= InputConfig.getButton(5);
            // Log.d("JOYRAW", device_id + " : " + "down");
        }

        if ((buffer[3] & 0x7F) == 0) {
            pad |= InputConfig.getButton(6);
            // Log.d("JOYRAW", device_id + " : " + "left");
        }
        if ((buffer[3] & 0x80) == 0x80) {
            pad |= InputConfig.getButton(7);
            // Log.d("JOYRAW", device_id + " : " + "right");
        }

        if ((buffer[5] & 0x2F) == 0x2F) {
            pad |= InputConfig.getButton(8);
            // Log.d("JOYRAW", device_id + " : " + "C");
        }

        if ((buffer[6] & 0x8) == 0x8) {
            pad |= InputConfig.getButton(9);
            // Log.d("JOYRAW", device_id + " : " + "Y");
        }

        if ((buffer[5] & 0x1F) == 0x1F) {
            pad |= InputConfig.getButton(10);
            // Log.d("JOYRAW", device_id + " : " + "X");
        }

        if ((buffer[6] & 0x2) == 0x2) {
            pad |= InputConfig.getButton(11);
            // Log.d("JOYRAW", device_id + " : " + "Z");
        }

        if ((buffer[6] & 0x4) == 0x4) {
            pad |= InputConfig.getButton(12);
            // Log.d("JOYRAW", device_id + " : " + "L1");
        }


        if ((buffer[6] & 0x1) == 0x1) {
            pad |= InputConfig.getButton(13);
            // Log.d("JOYRAW", device_id + " : " + "L2");
        }


        return pad;
    }

    /* daste havapeymaee hB */
    private int read4797P40993() {
        int pad = 0;

        if ((buffer[6] & 0x8) == 0x8) {
            pad |= InputConfig.getButton(0);
            // Log.d("JOYRAW", device_id + " : " + "B");
        }

        if ((buffer[6] & 0x2) == 0x2) {
            pad |= InputConfig.getButton(1);
            // Log.d("JOYRAW", device_id + " : " + "A");
        }

        if ((buffer[5] & 0x2) == 0x2) {
            pad |= InputConfig.getButton(2);
            // Log.d("JOYRAW", device_id + " : " + "MODE");
        }

        if ((buffer[6] & 0x10) == 0x10) {
            pad |= InputConfig.getButton(3);
            // Log.d("JOYRAW", device_id + " : " + "START");
        }

        if (buffer[1] < 0x7f && buffer[1] >= 0) {
            pad |= InputConfig.getButton(4);
            // Log.d("JOYRAW", device_id + " : " + "up");
        }

        if (buffer[1] < 0) {
            pad |= InputConfig.getButton(5);
            // Log.d("JOYRAW", device_id + " : " + "down");
        }

        if (buffer[0] < 0x7f && buffer[0] >= 0) {
            pad |= InputConfig.getButton(6);
            // Log.d("JOYRAW", device_id + " : " + "left");
        }
        if (buffer[0] < 0) {
            pad |= InputConfig.getButton(7);
            // Log.d("JOYRAW", device_id + " : " + "right");
        }

        if ((buffer[6] & 0x4) == 0x4) {
            pad |= InputConfig.getButton(8);
            // Log.d("JOYRAW", device_id + " : " + "C");
        }

        if ((buffer[6] & 0x20) == 0x20) {
            pad |= InputConfig.getButton(9);
            // Log.d("JOYRAW", device_id + " : " + "Y");
        }

        if ((buffer[6] & 0x1) == 0x1) {
            pad |= InputConfig.getButton(10);
            // Log.d("JOYRAW", device_id + " : " + "X");
        }

        if ((buffer[6] & 0x40) == 0x40) {
            pad |= InputConfig.getButton(11);
            // Log.d("JOYRAW", device_id + " : " + "Z");
        }

        if ((buffer[5] & 0x4) == 0x4) {
            pad |= InputConfig.getButton(12);
            // Log.d("JOYRAW", device_id + " : " + "L1");
        }


        if ((buffer[5] & 0x1) == 0x1) {
            pad |= InputConfig.getButton(13);
            // Log.d("JOYRAW", device_id + " : " + "L2");
        }


        return pad;
    }

    private int readV2079P58369() {
        int pad = 0;

        if ((buffer[5] & 0x4F) == 0x4F) {
            pad |= InputConfig.getButton(0);
            // Log.d("JOYRAW", device_id + " : " + "B");
        }

        if ((buffer[5] & 0x8F) == 0x8F) {
            pad |= InputConfig.getButton(1);
            // Log.d("JOYRAW", device_id + " : " + "A");
        }

        if ((buffer[6] & 0x10) == 0x10) {
            pad |= InputConfig.getButton(2);
            // Log.d("JOYRAW", device_id + " : " + "MODE");
        }

        if ((buffer[6] & 0x20) == 0x20) {
            pad |= InputConfig.getButton(3);
            // Log.d("JOYRAW", device_id + " : " + "START");
        }

        if ((buffer[1] & 0x7F) == 0) {
            pad |= InputConfig.getButton(4);
            // Log.d("JOYRAW", device_id + " : " + "up");
        }

        if ((buffer[1] & 0x80) == 0x80) {
            pad |= InputConfig.getButton(5);
            // Log.d("JOYRAW", device_id + " : " + "down");
        }

        if ((buffer[0] & 0x7F) == 0) {
            pad |= InputConfig.getButton(6);
            // Log.d("JOYRAW", device_id + " : " + "left");
        }
        if ((buffer[0] & 0x80) == 0x80) {
            pad |= InputConfig.getButton(7);
            // Log.d("JOYRAW", device_id + " : " + "right");
        }

        if ((buffer[5] & 0x2F) == 0x2F) {
            pad |= InputConfig.getButton(8);
            // Log.d("JOYRAW", device_id + " : " + "C");
        }

        if ((buffer[6] & 0x2) == 0x2) {
            pad |= InputConfig.getButton(9);
            // Log.d("JOYRAW", device_id + " : " + "Y");
        }

        if ((buffer[5] & 0x1F) == 0x1F) {
            pad |= InputConfig.getButton(10);
            // Log.d("JOYRAW", device_id + " : " + "X");
        }

        if ((buffer[6] & 0x8) == 0x8) {
            pad |= InputConfig.getButton(11);
            // Log.d("JOYRAW", device_id + " : " + "Z");
        }

        if ((buffer[6] & 0x1) == 0x1) {
            pad |= InputConfig.getButton(12);
            // Log.d("JOYRAW", device_id + " : " + "L1");
        }


        if ((buffer[6] & 0x4) == 0x4) {
            pad |= InputConfig.getButton(13);
            // Log.d("JOYRAW", device_id + " : " + "L2");
        }


        return pad;
    }

    private int readV2064P1() {
        int pad = 0;

        if ((buffer[5] & 0x4F) == 0x4F) {
            pad |= InputConfig.getButton(0);
            // Log.d("JOYRAW", device_id + " : " + "B");
        }

        if ((buffer[5] & 0x8F) == 0x8F) {
            pad |= InputConfig.getButton(1);
            // Log.d("JOYRAW", device_id + " : " + "A");
        }

        if ((buffer[6] & 0x10) == 0x10) {
            pad |= InputConfig.getButton(2);
            // Log.d("JOYRAW", device_id + " : " + "MODE");
        }

        if ((buffer[6] & 0x20) == 0x20) {
            pad |= InputConfig.getButton(3);
            // Log.d("JOYRAW", device_id + " : " + "START");
        }

        if ((buffer[4] & 0x7F) == 0) {
            pad |= InputConfig.getButton(4);
            // Log.d("JOYRAW", device_id + " : " + "up");
        }

        if ((buffer[4] & 0x80) == 0x80) {
            pad |= InputConfig.getButton(5);
            // Log.d("JOYRAW", device_id + " : " + "down");
        }

        if ((buffer[3] & 0x7F) == 0) {
            pad |= InputConfig.getButton(6);
            // Log.d("JOYRAW", device_id + " : " + "left");
        }
        if ((buffer[3] & 0x80) == 0x80) {
            pad |= InputConfig.getButton(7);
            // Log.d("JOYRAW", device_id + " : " + "right");
        }

        if ((buffer[5] & 0x2F) == 0x2F) {
            pad |= InputConfig.getButton(8);
            // Log.d("JOYRAW", device_id + " : " + "C");
        }

        if ((buffer[6] & 0x2) == 0x2) {
            pad |= InputConfig.getButton(9);
            // Log.d("JOYRAW", device_id + " : " + "Y");
        }

        if ((buffer[5] & 0x1F) == 0x1F) {
            pad |= InputConfig.getButton(10);
            // Log.d("JOYRAW", device_id + " : " + "X");
        }

        if ((buffer[6] & 0x8) == 0x8) {
            pad |= InputConfig.getButton(11);
            // Log.d("JOYRAW", device_id + " : " + "Z");
        }

        if ((buffer[6] & 0x1) == 0x1) {
            pad |= InputConfig.getButton(12);
            // Log.d("JOYRAW", device_id + " : " + "L1");
        }


        if ((buffer[6] & 0x4) == 0x4) {
            pad |= InputConfig.getButton(13);
            // Log.d("JOYRAW", device_id + " : " + "L2");
        }


        return pad;
    }

    private int readV121P6() {
        int pad = 0;

        if ((buffer[5] & 0x4F) == 0x4F) {
            pad |= InputConfig.getButton(0);
            // Log.d("JOYRAW", device_id + " : " + "B");
        }

        if ((buffer[5] & 0x8F) == 0x8F) {
            pad |= InputConfig.getButton(1);
            // Log.d("JOYRAW", device_id + " : " + "A");
        }

        if ((buffer[6] & 0x10) == 0x10) {
            pad |= InputConfig.getButton(2);
            // Log.d("JOYRAW", device_id + " : " + "MODE");
        }

        if ((buffer[6] & 0x20) == 0x20) {
            pad |= InputConfig.getButton(3);
            // Log.d("JOYRAW", device_id + " : " + "START");
        }

        if ((buffer[1] & 0x7F) == 0) {
            pad |= InputConfig.getButton(4);
            // Log.d("JOYRAW", device_id + " : " + "up");
        }

        if ((buffer[1] & 0x80) == 0x80) {
            pad |= InputConfig.getButton(5);
            // Log.d("JOYRAW", device_id + " : " + "down");
        }

        if ((buffer[0] & 0x7F) == 0) {
            pad |= InputConfig.getButton(6);
            // Log.d("JOYRAW", device_id + " : " + "left");
        }
        if ((buffer[0] & 0x80) == 0x80) {
            pad |= InputConfig.getButton(7);
            // Log.d("JOYRAW", device_id + " : " + "right");
        }

        if ((buffer[5] & 0x2F) == 0x2F) {
            pad |= InputConfig.getButton(8);
            // Log.d("JOYRAW", device_id + " : " + "C");
        }

        if ((buffer[6] & 0x2) == 0x2) {
            pad |= InputConfig.getButton(9);
            // Log.d("JOYRAW", device_id + " : " + "Y");
        }

        if ((buffer[5] & 0x1F) == 0x1F) {
            pad |= InputConfig.getButton(10);
            // Log.d("JOYRAW", device_id + " : " + "X");
        }

        if ((buffer[6] & 0x8) == 0x8) {
            pad |= InputConfig.getButton(11);
            // Log.d("JOYRAW", device_id + " : " + "Z");
        }

        if ((buffer[6] & 0x1) == 0x1) {
            pad |= InputConfig.getButton(12);
            // Log.d("JOYRAW", device_id + " : " + "L1");
        }


        if ((buffer[6] & 0x4) == 0x4) {
            pad |= InputConfig.getButton(13);
            // Log.d("JOYRAW", device_id + " : " + "L2");
        }


        return pad;
    }

    @Override
    public boolean equals(Object o) {
        if (device != null && ((ThreadUSB) o).device != null) {
            return (device.getProductId() == ((ThreadUSB) o).device.getProductId());
        }
        return super.equals(o);
    }
}
