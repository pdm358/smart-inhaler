package com.ybeltagy.breathe.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.ybeltagy.breathe.R;
import com.ybeltagy.breathe.collection.Export;
import com.ybeltagy.breathe.data.InhalerUsageEvent;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private List<InhalerUsageEvent> IUEList;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        // MainActivity's interactions with the data layer are through BreatheViewModel alone
        // view model for the recyclerview
        BreatheViewModel breatheViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(BreatheViewModel.class);

        //fixme: I'm not sure constantly updating the list is the most efficient way to do this.
        breatheViewModel.getAllInhalerUsageEvents().observe(this, inhalerUsageEvents -> this.IUEList = inhalerUsageEvents);

    }

    //fixme: it is better to use breatheViewModel as an intermediary step.
    public void exportAllIUEs(View view){

        Intent fileIntent = Export.extractAllIUE(this, IUEList);
        if(fileIntent != null)startActivity(fileIntent); // fixme: consider making this a startActivityForIntent.
        else Toast.makeText(this, "Failed to Export Data", Toast.LENGTH_SHORT).show();

    }

}
