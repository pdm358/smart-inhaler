package com.ybeltagy.breathe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.os.Bundle;

import java.util.List;

/**
 * This activity contains the main logic of the Breathe app. It renders the UI and registers a
 * Bluetooth Broadcast receiver to listen for Bluetooth connection and disconnection to the phone
 * (to be changed to BLE).
 */
public class MainActivity extends AppCompatActivity {

    // MainActivity's interactions with the data layer are through BreatheViewModel alone
    // Note: did not make this local because we might reference this when updating other UI fields
    private BreatheViewModel breatheViewModel;

    // Intent extra for Diary Entry Activity
    // TODO: extract to string resource
    public static final String EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT =
            "extra_inhaler_usage_event_to_be_updated";

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
        // set doses taken shown in the ProgressBar to fake data increment (20 fake IUE events)
        // TODO: Is it normal to use the same view model for both the diary timeline and the
        //       progress bar? Will this cause problems later?
        breatheViewModel.getAllInhalerUsageEvents().observe(this, new Observer<List<InhalerUsageEvent>>() {
            @Override
            public void onChanged(List<InhalerUsageEvent> inhalerUsageEvents) {
                Log.d("MainActivity", "setting progress to " + inhalerUsageEvents.size());
                medicineStatusBar.setProgress(inhalerUsageEvents.size());

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
        Log.d("MainActivity", "DiaryView starting to render...");

        // make adapter and provide data to be displayed
        IUEListAdapter iueListAdapter = new IUEListAdapter(this);
        // connect adapter and recyclerView
        iueRecyclerView.setAdapter(iueListAdapter);
        // set an on-click listener so we can get the InhalerUsageEvent at the clicked positon and
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

        // get handle to the BreatheViewModel
        // Note: constructor in codelab did not work; searched for a long time and this fixed it:
        // https://github.com/googlecodelabs/android-room-with-a-view/issues/145#issuecomment-739756244
        breatheViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(BreatheViewModel.class);

        // Updated cached copy of InhalerUsageEvents
        breatheViewModel.getAllInhalerUsageEvents().observe(this, inhalerUsageEvents1 -> {
            iueListAdapter.setWords(inhalerUsageEvents1);
            Log.d("MainActivity", "database changed - added IUEs");
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    // TODO: if we change the primary key of the InhalerUsageEvent to a random id, the intent
    //       will also need to include the InhalerUsageEvent's id
    public void launchDiaryEntryActivity(InhalerUsageEvent inhalerUsageEvent) {
        Intent intent = new Intent(this, DiaryEntryActivity.class);
        intent.putExtra(EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT, inhalerUsageEvent.getInhalerUsageEventTimeStamp());
        startActivity(intent);
    }
}