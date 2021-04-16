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
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ybeltagy.breathe.MainActivity.EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_EXISTING_MESSAGE;
import static com.ybeltagy.breathe.MainActivity.EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TIMESTAMP_KEY;
import static com.ybeltagy.breathe.Tag.PREVENTATIVE;
import static com.ybeltagy.breathe.Tag.RESCUE;

public class DiaryEntryActivity extends AppCompatActivity {

    // DiaryEntryActivity's interactions with the data layer are through BreatheViewModel alone
    private BreatheViewModel breatheViewModel;

    // assumes there are only 2 mutually exclusive flags: Preventative and Rescue
    private AtomicBoolean tagIsPreventative = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entry);

        Log.d("DiaryEntryActivity", "super.onCreate() and setContentView() finished");

        EditText messageEditText = findViewById(R.id.edit_diary_edittext);

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

            setExistingDiaryMessage(diaryEntryMessage, messageEditText);

            setUpSaveButton(timeStampString, messageEditText);
            setUpPreventativeTagButton();
            setUpRescueTagButton();

        }
    }

    public void setHeaderDateForInhalerUsageEvent(String timeStampString) {
        // set text to show which InhalerUsageEvent the user is adding/editing a message to
        TextView eventTimeStamp = findViewById(R.id.entry_date_textview);
        eventTimeStamp.setText(String.format("Entry for %s", timeStampString));
    }

    public void setExistingDiaryMessage(String diaryEntryMessage, EditText messageEditText) {
        // if there is already a message for this IUE, display it so the user may edit it
        if (!diaryEntryMessage.isEmpty()) {
            messageEditText.setText(diaryEntryMessage);
            messageEditText.setSelection(diaryEntryMessage.length());
            messageEditText.requestFocus();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setUpSaveButton(String timeStampString, EditText messageEditText) {
        // save button
        final Button saveButton = findViewById(R.id.diary_entry_save_button);
        // When the user presses the Save button, create a new Intent for the reply.
        // The reply Intent will be sent back to the calling activity (in this case, MainActivity).
        saveButton.setOnClickListener(view -> {
            // get current tag
            Tag tag = null;
            if (tagIsPreventative != null) {
                tag = tagIsPreventative.get() ? PREVENTATIVE : RESCUE;
            }
            // save to the database directly here
            breatheViewModel.update(new InhalerUsageEvent(Instant.parse(timeStampString),
                    null, new DiaryEntry(tag, messageEditText.getText().toString()), null));

            finish(); // stop this activity
        });
    }

    public void setUpPreventativeTagButton() {
        // preventative tag button
        final Button preventative = findViewById(R.id.preventative_button);
        preventative.setOnClickListener(view -> tagIsPreventative = new AtomicBoolean(true));
    }

    public void setUpRescueTagButton() {
        // rescue tag button
        final Button rescue = findViewById(R.id.rescue_button);
        rescue.setOnClickListener(view -> tagIsPreventative = new AtomicBoolean(false));
    }
}