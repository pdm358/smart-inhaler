package com.ybeltagy.breathe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import static com.ybeltagy.breathe.MainActivity.EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TIMESTAMP;

public class DiaryEntryActivity extends AppCompatActivity {

    // EditText view - where the user enters the diary entry message
    private EditText messageEditText;

    // TODO: extract string resources
    public static final String EXTRA_DIARY_MESSAGE_REPLY = "DIARY_MESSAGE_REPLY";
    public static final String EXTRA_DIARY_MESSAGE_REPLY_ID = "DIARY_MESSAGE_REPLY_ID";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entry);

        Log.d("DiaryEntryActivity", "super.onCreate() and setContentView() finished");

        messageEditText = findViewById(R.id.edit_diary_edittext);

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // The clicked InhalerUsageEvent's Instant timestamp
            String timeStampString = extras.getString(EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TIMESTAMP);
            setHeaderDateForInhalerUsageEvent(timeStampString);

            String diaryEntryMessage = extras.getString(EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_EXISTING_MESSAGE);

            Instant inhalerUsageEventTimestamp = Converters.fromTimeStampString(timeStampString);
            // if there is already a message for this IUE, display it so the user may edit it
            if (!diaryEntryMessage.isEmpty()){
                messageEditText.setText(diaryEntryMessage);
                messageEditText.setSelection(diaryEntryMessage.length());
                messageEditText.requestFocus();
            }

            final Button saveButton = findViewById(R.id.diary_entry_save_button);
            // When the user presses the Save button, create a new Intent for the reply.
            // The reply Intent will be sent back to the calling activity (in this case, MainActivity).
            saveButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    // Create a new Intent for the reply.
                    Intent replyIntent = new Intent();
                    if (TextUtils.isEmpty(messageEditText.getText())) {
                        // No message was entered, set the result accordingly.
                        setResult(RESULT_CANCELED, replyIntent);
                    } else {
                        // Get the new message that the user entered.
                        String diaryMessage = messageEditText.getText().toString();
                        // Put the new diaryMessage in the extras for the reply Intent.
                        replyIntent.putExtra(EXTRA_DIARY_MESSAGE_REPLY, diaryMessage);
                        if (extras != null && extras.containsKey(EXTRA_DIARY_MESSAGE_REPLY_ID)) {
                            int id = extras.getInt(EXTRA_DIARY_MESSAGE_REPLY_ID, -1);
                            if (id != -1) {
                                replyIntent.putExtra(EXTRA_DIARY_MESSAGE_REPLY_ID, id);
                            }
                        }
                        // Set the result status to indicate success.
                        setResult(RESULT_OK, replyIntent);
                    }
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