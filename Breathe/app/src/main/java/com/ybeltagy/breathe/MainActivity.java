package com.ybeltagy.breathe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

/**
 * This activity contains the main logic of the Breathe app. It renders the UI and registers a
 * Bluetooth Broadcast receiver to listen for Bluetooth connection and disconnection to the phone
 * (to be changed to BLE).
 */
public class MainActivity extends AppCompatActivity {

    // MainActivity's interactions with the data layer are through BreatheViewModel alone
    private BreatheViewModel diaryTimelineViewModel; // view model for the recyclerview
    private BreatheViewModel progressBarViewModel;   // view model for the progressbar

    public static final int UPDATE_INHALER_USAGE_EVENT_REQUEST_CODE = 1;

    // Intent extra for Diary Entry Activity
    // TODO: extract these to string resources?
    public static final String EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TIMESTAMP_KEY =
            "extra_inhaler_usage_event_to_be_updated_timestamp";
    public static final String EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_EXISTING_MESSAGE =
            "extra_inhaler_usage_event_to_be_updated_existing_message";
    public static final String EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TAG =
            "extra_inhaler_usage_event_to_be_updated_existing_tag";

    // for GPS location
    // TODO: move to data collection service that is triggered by receiving a timestamp
    private FusedLocationProviderClient fusedLocationProviderClient;

    // debug
    String MAIN_ACTIVITY_LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView ----------------------------------------------------------------------------
        // populate the fake data for the RecyclerView
        renderDiaryView();

        // fake data - assumed number of doses in a canister
        // TODO: Replace with real number of doses in a canister
        //  or put that in the resources folder if we will continue using it.
        int totalDosesInCanister = 200;

        // ProgressBar -----------------------------------------------------------------------------
        renderMedStatusView(totalDosesInCanister);

        // Location client
        // TODO: move to data collection service that is triggered by receiving a timestamp
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // TODO: request permissions if not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d(MAIN_ACTIVITY_LOG_TAG, "Got location: "
                            + location.getLatitude() + " " + location.getLongitude());
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // render the Progress Bar for the medicine status in first pane (top of the screen)
    private void renderMedStatusView(int totalDosesInCanister) {
        ProgressBar medicineStatusBar = findViewById(R.id.doses_progressbar);

        // update max amount of progress bar to number of doses in a full medicine canister
        medicineStatusBar.setMax(totalDosesInCanister);

        // set doses taken shown in the ProgressBar
        progressBarViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(BreatheViewModel.class);
        progressBarViewModel.getAllInhalerUsageEvents().observe(this, new Observer<List<InhalerUsageEvent>>() {
            @Override
            public void onChanged(List<InhalerUsageEvent> inhalerUsageEvents) {
                // medicine left is number of doses in a full container - doses used
                medicineStatusBar.setProgress(medicineStatusBar.getMax() - inhalerUsageEvents.size());

                // set text to show how many doses have been taken
                TextView dosesTakenText = findViewById(R.id.doses_textview);
                dosesTakenText.setText(String.format("%d / %d", medicineStatusBar.getProgress(), medicineStatusBar.getMax()));
            }
        });
    }

    // render the diary RecyclerView for the diary timeline of events in third pane
    // (bottom of screen)
    private void renderDiaryView() {
        RecyclerView iueRecyclerView = findViewById(R.id.diary_recyclerview);
        Log.d(MAIN_ACTIVITY_LOG_TAG, "DiaryView starting to render...");

        // make adapter and provide data to be displayed
        IUEListAdapter iueListAdapter = new IUEListAdapter(this);

        // connect adapter and recyclerView
        iueRecyclerView.setAdapter(iueListAdapter);

        // set an on-click listener so we can get the InhalerUsageEvent at the clicked position and
        // pass it to the DiaryEntryActivity
        iueListAdapter.setOnItemClickListener(new IUEListAdapter.ClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(View v, int position) {
                launchDiaryEntryActivity(iueListAdapter.getInhalerUsageEventAtPosition(position));
            }
        });
        // set layout manager for recyclerView
        iueRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Note: constructor in codelab did not work; searched for a long time and this fixed it:
        // https://github.com/googlecodelabs/android-room-with-a-view/issues/145#issuecomment-739756244
        diaryTimelineViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(BreatheViewModel.class);

        // Updated cached copy of InhalerUsageEvents
        diaryTimelineViewModel.getAllInhalerUsageEvents().observe(this, inhalerUsageEvents1 -> {
            iueListAdapter.setWords(inhalerUsageEvents1);
            Log.d(MAIN_ACTIVITY_LOG_TAG, "database changed - added IUEs");
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void launchDiaryEntryActivity(InhalerUsageEvent inhalerUsageEvent) {
        Log.d("MAIN_ACTIVITY_LOG_TAG", "Launching diary entry activity!");
        Intent intent = new Intent(this, DiaryEntryActivity.class);

        // add timestamp
        intent.putExtra(EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TIMESTAMP_KEY,
                inhalerUsageEvent.getInhalerUsageEventTimeStamp().toString());

        // add existing message
        String existingMessage = inhalerUsageEvent.getDiaryEntry() != null ?
                (inhalerUsageEvent.getDiaryEntry().getMessage()) : ("");
        intent.putExtra(EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_EXISTING_MESSAGE, existingMessage);

        // add existing tag, if any
        if (inhalerUsageEvent.getDiaryEntry() != null &&
                inhalerUsageEvent.getDiaryEntry().getTag() != null) {
            intent.putExtra(EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TAG, inhalerUsageEvent.getDiaryEntry().getTag());
        }
        startActivityForResult(intent, UPDATE_INHALER_USAGE_EVENT_REQUEST_CODE);
    }
}