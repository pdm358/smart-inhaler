package com.ybeltagy.breathe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    // third pane (bottom of screen) -> shows diary timeline of events
    private RecyclerView iueRecyclerView;
    private IUEListAdapter iueListAdapter;

    // fake data for recyclerView
    private final LinkedList<String> eventList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView (bottom pane) --------------------------------------------------------------

        // populate the fake data for the RecyclerView
        // Todo:: delete and replace with real data passed to the IUEListAdapter constructor
        for (int i = 1; i <= 20; i++) {
            Date date = new Date(2020,10, i);
            eventList.addLast(date.toString());
        }

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