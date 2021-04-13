package com.ybeltagy.breathe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.time.Instant;

import static com.ybeltagy.breathe.MainActivity.EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT;

public class DiaryEntryActivity extends AppCompatActivity {

    // EditText view - where the user enters the diary entry message
    private EditText messageEditText;

    // DiaryEntry's interactions with the data layer are through BreatheViewModel alone
    // Note: did not make this local because we might reference this when updating other UI fields
    private BreatheViewModel breatheViewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entry);

        Log.d("DiaryEntryActivity", "super.onCreate() and setContentView() finished");

        // setup the BreatheViewModel
        breatheViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(BreatheViewModel.class);

        messageEditText = findViewById(R.id.edit_diary_edittext);

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // The clicked InhalerUsageEvent's Instant timestamp
            String timeStampString = extras.getString(EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT);
            Instant inhalerUsageEventTimestamp = Converters.fromTimeStampString(timeStampString);
            Log.d("DiaryEntryActivity", "Timestamp received was " + timeStampString);
            // set text to show which InhalerUsageEvent the user is adding/editing a message to
            TextView eventTimeStamp = findViewById(R.id.entry_date_textview);
            eventTimeStamp.setText(String.format("Entry for %s", timeStampString));
        }
    }
}