package com.ybeltagy.breathe.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.os.Bundle;
import android.widget.Toast;

import com.ybeltagy.breathe.ble.BLEScanner;
import com.ybeltagy.breathe.ble.BLEService;
import com.ybeltagy.breathe.collection.Export;
import com.ybeltagy.breathe.data.InhalerUsageEvent;
import com.ybeltagy.breathe.R;
import com.ybeltagy.breathe.data.WearableData;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

/**
 * This activity contains the main logic of the Breathe app. It renders the UI and registers a
 * Bluetooth Broadcast receiver to listen for Bluetooth connection and disconnection to the phone
 * (to be changed to BLE).
 */
public class MainActivity extends AppCompatActivity {

    // MainActivity's interactions with the data layer are through BreatheViewModel alone
    private BreatheViewModel breatheViewModel; // view model for the recyclerview

    private static final String tag = MainActivity.class.getName(); // Maybe we can use this, going forward.

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //fixme: is there a better way to detect the app was opened?
        Intent foregroundServiceIntent = new Intent(this, BLEService.class);
        startForegroundService(foregroundServiceIntent); //FIXME why does it take a context too?

        // Note: constructor in code lab did not work; searched for a long time and this fixed it:
        // https://github.com/googlecodelabs/android-room-with-a-view/issues/145#issuecomment-739756244
        breatheViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(BreatheViewModel.class);

        // ProgressBar
        renderMedStatusView(UIFinals.TOTAL_DOSES_IN_CANISTER);

        // RecyclerView
        // populate the fake data for the RecyclerView using
        renderDiaryView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the toolbar menu.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Handle toolbar clicks.

        int id = item.getItemId(); // which item was clicked.

        if(id == R.id.settings_menuitem){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent); // fixme: should this be for a result?
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * render the Progress Bar for the medicine status in first pane (top of the screen)
     * @param totalDosesInCanister the size of the inhaler canister
     */
    private void renderMedStatusView(int totalDosesInCanister) {
        ProgressBar medicineStatusBar = findViewById(R.id.doses_progressbar);

        // update max amount of progress bar to number of doses in a full medicine canister
        medicineStatusBar.setMax(totalDosesInCanister);

        //Get text view to set the text for.
        TextView dosesTakenText = findViewById(R.id.doses_textview);

        breatheViewModel.getAllInhalerUsageEvents().observe(this, inhalerUsageEvents -> {
            // medicine left is number of doses in a full container - doses used
            medicineStatusBar.setProgress(medicineStatusBar.getMax() - inhalerUsageEvents.size());
            dosesTakenText.setText(String.format(Locale.ENGLISH,"%d / %d", medicineStatusBar.getProgress(), medicineStatusBar.getMax()));
        });
    }

    /**
     * render the diary RecyclerView for the diary timeline of events in third pane
     */
    @SuppressLint("NewApi")
    private void renderDiaryView() {

        Log.d(tag, "DiaryView starting to render...");

        RecyclerView iueRecyclerView = findViewById(R.id.diary_recyclerview);

        IUEListAdapter iueListAdapter = new IUEListAdapter(this); // make adapter and provide data to be displayed

        // set an on-click listener so we can get the InhalerUsageEvent at the clicked position and
        // pass it to the DiaryEntryActivity
        iueListAdapter.setOnItemClickListener((v, position) -> launchDiaryEntryActivity(iueListAdapter.getInhalerUsageEventAtPosition(position)));

        iueRecyclerView.setAdapter(iueListAdapter); // connect adapter and recyclerView

        // set layout manager for recyclerView
        iueRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Updated cached copy of InhalerUsageEvents
        breatheViewModel.getAllInhalerUsageEvents().observe(this, inhalerUsageEvents -> {
            // update all the dataset of the recycler view for any change in the inhaler usage events.
            // I'm not sure this is the most efficient way to do it, but it is guaranteed to work.
            iueListAdapter.setIUEs(inhalerUsageEvents);
            Log.d(tag, "database changed - added IUEs");
        });
    }

    /**
     * Opens the diary entry activity for the passed inhalerUsageEvent.
     * todo: as the project develops, consider not not passing anymore than than IUE timestamp in the intent and retrieving
     *  everything from the database in the DiaryEntryActivity
     * @param inhalerUsageEvent the iue to edit the diary for
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void launchDiaryEntryActivity(InhalerUsageEvent inhalerUsageEvent) {

        // don't do anything for invalid input.
        if(inhalerUsageEvent == null || inhalerUsageEvent.getInhalerUsageEventTimeStamp() == null) return;

        Log.d(tag, "Launching diary entry activity!");

        Intent intent = new Intent(this, DiaryEntryActivity.class);

        // send timestamp
        intent.putExtra(UIFinals.EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TIMESTAMP_KEY,
                inhalerUsageEvent.getInhalerUsageEventTimeStamp().toString());

        intent.putExtra(UIFinals.EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_EXISTING_MESSAGE,
                inhalerUsageEvent.getDiaryEntry().getMessage());

        intent.putExtra(UIFinals.EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TAG,
                inhalerUsageEvent.getDiaryEntry().getTag());

        startActivityForResult(intent, UIFinals.UPDATE_INHALER_USAGE_EVENT_REQUEST_CODE);

    }

}