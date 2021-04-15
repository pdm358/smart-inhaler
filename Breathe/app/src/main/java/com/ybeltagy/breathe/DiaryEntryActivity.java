package com.ybeltagy.breathe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.time.Instant;

import static com.ybeltagy.breathe.MainActivity.EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_EXISTING_MESSAGE;
import static com.ybeltagy.breathe.MainActivity.EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TIMESTAMP_KEY;
import static com.ybeltagy.breathe.Tag.PREVENTATIVE;

public class DiaryEntryActivity extends AppCompatActivity {

    // EditText view - where the user enters the diary entry message
    private EditText messageEditText;

    // DiaryEntryActivity's interactions with the data layer are through BreatheViewModel alone
    BreatheViewModel breatheViewModel;

    // TODO: extract string resources
    public static final String EXTRA_DIARY_MESSAGE_REPLY = "DIARY_MESSAGE_REPLY";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entry);

        Log.d("DiaryEntryActivity", "super.onCreate() and setContentView() finished");

        messageEditText = findViewById(R.id.edit_diary_edittext);

        // construct the view model to the Breathe DB
        breatheViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(BreatheViewModel.class);

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // The clicked InhalerUsageEvent's Instant timestamp
            String timeStampString = extras.getString(EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TIMESTAMP_KEY);
            setHeaderDateForInhalerUsageEvent(timeStampString);

            String diaryEntryMessage = extras.getString(EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_EXISTING_MESSAGE);

            // if there is already a message for this IUE, display it so the user may edit it
            if (!diaryEntryMessage.isEmpty()) {
                messageEditText.setText(diaryEntryMessage);
                messageEditText.setSelection(diaryEntryMessage.length());
                messageEditText.requestFocus();
            }

            final Button saveButton = findViewById(R.id.diary_entry_save_button);
            // When the user presses the Save button, create a new Intent for the reply.
            // The reply Intent will be sent back to the calling activity (in this case, MainActivity).
            saveButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    // Instead of sending an intent back to the main activity for the main
                    // activity to save to the database, let's save to the database directly here
                    breatheViewModel.update(new InhalerUsageEvent(Instant.parse(timeStampString),
                        null, new DiaryEntry(PREVENTATIVE, messageEditText.getText().toString()), null));

                    finish();
                }
            });
        }
    }

    public void setHeaderDateForInhalerUsageEvent(String timeStampString) {
        // set text to show which InhalerUsageEvent the user is adding/editing a message to
        TextView eventTimeStamp = findViewById(R.id.entry_date_textview);
        eventTimeStamp.setText(String.format("Entry for %s", timeStampString));
    }
}