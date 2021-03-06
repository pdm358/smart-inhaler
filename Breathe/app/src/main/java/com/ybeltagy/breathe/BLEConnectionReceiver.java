package com.ybeltagy.breathe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

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
                testToast = "Bluetooth disconnected meow!";
                break;
        }
        Toast.makeText(context, testToast, Toast.LENGTH_SHORT).show();

    }
}