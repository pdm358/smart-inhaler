package com.ybeltagy.breathe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    // fake data - assumed number of doses in a canister
    // Todo:: replace with real number of doses in a canister
    final int TOTAL_DOSES_IN_CANISTER = 200;

    // first pane (top of the screen) -> shows medicine left
    private ProgressBar medicineStatusBar;
    private TextView dosesTakenText;

    // third pane (bottom of screen) -> shows diary timeline of events
    private RecyclerView iueRecyclerView;
    private IUEListAdapter iueListAdapter;

    // fake data for RecyclerView
    private final LinkedList<String> eventList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView ----------------------------------------------------------------------------
        // populate the fake data for the RecyclerView
        // Todo:: delete and replace with real data passed to renderDiaryView()
        for (int i = 1; i <= 20; i++) {
            Date date = new Date(2020, 10, i);
            eventList.addLast(date.toString());
        }
        renderDiaryView(eventList);

        // ProgressBar -----------------------------------------------------------------------------
        renderMedStatusView();
    }

    // render the Progress Bar for the medicine status
    private void renderMedStatusView() {
        // handle to the ProgressBar
        medicineStatusBar = findViewById(R.id.determinateProgressBar);
        // update max amount of progress bar to number of doses in a full medicine canister
        medicineStatusBar.setMax(TOTAL_DOSES_IN_CANISTER);
        // set doses taken shown in the ProgressBar to fake data increment (20 fake IUE events)
        // Todo:: delete fake data increment, replace with real increment (# of newly synced IUEs)
        medicineStatusBar.setProgress(eventList.size());

        // set text to show how many doses have been taken
        dosesTakenText = findViewById(R.id.doses_taken_label);
        dosesTakenText.setText(medicineStatusBar.getProgress() + " / " + medicineStatusBar.getMax());
    }

    // render the diary RecyclerView for the diary timeline
    private void renderDiaryView(LinkedList<String> eventList) {
        // handle to the RecyclerView
        iueRecyclerView = findViewById(R.id.recyclerView);
        // make adapter and provide data to be displayed
        iueListAdapter = new IUEListAdapter(this, eventList);
        // connect adapter and recyclerView
        iueRecyclerView.setAdapter(iueListAdapter);
        // set layout manager for recyclerView
        iueRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}