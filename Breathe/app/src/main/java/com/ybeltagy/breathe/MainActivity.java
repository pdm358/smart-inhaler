package com.ybeltagy.breathe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ProgressBar;
import android.widget.TextView;

import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.Date;
import java.util.LinkedList;

/**
 * This activity contains the main logic of the Breathe app. It renders the UI and registers a
 * Bluetooth Broadcast receiver to listen for Bluetooth connection and disconnection to the phone
 * (to be changed to BLE).
 *
 * @author Sarah Panther
 */
public class MainActivity extends AppCompatActivity {

    // fake data - assumed number of doses in a canister
    // TODO: Replace with real number of doses in a canister
    final int TOTAL_DOSES_IN_CANISTER = 200;

    // fake data for RecyclerView
    private final LinkedList<String> eventList = new LinkedList<>();

    // BroadcastReceiver for Bluetooth
    // TODO: Change to BLE
    private final BLEConnectionReceiver bleBroadcastReceiver = new BLEConnectionReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView ----------------------------------------------------------------------------
        // populate the fake data for the RecyclerView
        // TODO: Delete and replace with real data passed to renderDiaryView()
        for (int i = 1; i <= 20; i++) {
            Date date = new Date(2020, 10, i);
            eventList.addLast(date.toString());
        }
        renderDiaryView(eventList);

        // ProgressBar -----------------------------------------------------------------------------
        renderMedStatusView();

        // BroadcastReceiver for Bluetooth ---------------------------------------------------------
        // TODO: Change to BLE
        IntentFilter filterConnection = new IntentFilter();
        filterConnection.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filterConnection.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        // register BroadcastReceiver with the Android OS
        this.registerReceiver(bleBroadcastReceiver, filterConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // render the Progress Bar for the medicine status in first pane (top of the screen)
    private void renderMedStatusView() {
        ProgressBar medicineStatusBar = findViewById(R.id.determinateProgressBar);
        // update max amount of progress bar to number of doses in a full medicine canister
        medicineStatusBar.setMax(TOTAL_DOSES_IN_CANISTER);
        // set doses taken shown in the ProgressBar to fake data increment (20 fake IUE events)
        // TODO: Delete fake data increment, replace with real increment (# of newly synced IUEs)
        medicineStatusBar.setProgress(eventList.size());

        // set text to show how many doses have been taken
        TextView dosesTakenText = findViewById(R.id.doses_taken_label);
        dosesTakenText.setText(String.format("%d / %d", medicineStatusBar.getProgress(), medicineStatusBar.getMax()));
    }

    // render the diary RecyclerView for the diary timeline of events in third pane
    // (bottom of screen)
    private void renderDiaryView(LinkedList<String> eventList) {
        RecyclerView iueRecyclerView = findViewById(R.id.recyclerView);
        // make adapter and provide data to be displayed
        IUEListAdapter iueListAdapter = new IUEListAdapter(this, eventList);
        // connect adapter and recyclerView
        iueRecyclerView.setAdapter(iueListAdapter);
        // set layout manager for recyclerView
        iueRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}