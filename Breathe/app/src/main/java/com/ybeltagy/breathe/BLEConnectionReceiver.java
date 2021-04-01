package com.ybeltagy.breathe;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * This BroadcastReceiver is triggered by Bluetooth connection and disconnection intents by a paired
 * secondary device to the phone. (To be changed to BLE connection and disconnection)
 *
 * @author Sarah Panther
 */
public class BLEConnectionReceiver extends BroadcastReceiver {
    @Override
    // TODO: Change to BLE
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        String testToast = "Nothing to show meow";
        switch (action) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                testToast = "Bluetooth connected meow!";
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                testToast = "Disconnected from Bluetooth meow!";
                break;
        }
        Toast.makeText(context, testToast, Toast.LENGTH_SHORT).show();
    }
}