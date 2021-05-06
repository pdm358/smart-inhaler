package com.ybeltagy.breathe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.os.Bundle;

import java.util.Calendar;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.ybeltagy.breathe.WeatherAPIWorker.KEY_WEATHER_DATA_RESULT;

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

    // FIXME: find a better place to put this and a better way to do this (probably a map)
    String levelValuesToText[] = {"None", "Very Low", "Low", "Medium", "High", "Very High", "--"};

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

        // FIXME: move to data collection flow (here for testing)
        // WorkManager -> gets WeatherData
        weatherDataFlow();
    }

    // FIXME: move to data collection flow (here for testing)
    private void weatherDataFlow() {
        // create work request for GPS
        WorkManager dataFlowManager = WorkManager.getInstance(getApplication());
        OneTimeWorkRequest gpsRequest = new OneTimeWorkRequest.Builder(GPSWorker.class)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS).build();

        Data.Builder data = new Data.Builder();
        double testArray[] = {47.622743, -122.2929146};
        data.putDoubleArray(GPSWorker.KEY_GPS_RESULT, testArray);

        //  create work request for online weather data for the GPS location
        OneTimeWorkRequest weatherAPIRequest = new OneTimeWorkRequest.Builder(WeatherAPIWorker.class)
                .setInputData(data.build()).build();

        dataFlowManager.enqueue(gpsRequest);
        dataFlowManager.enqueue(weatherAPIRequest);

        displayWeatherData(weatherAPIRequest.getId());
    }

    private void displayWeatherData(UUID weatherAPIRequestID) {
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(weatherAPIRequestID)
                .observe(this, info -> {
                    if (info != null && info.getState().isFinished()) {
                        Log.d(MAIN_ACTIVITY_LOG_TAG, "Got data back from WeatherAPITask");
                        String serializedWeatherData = info.getOutputData().getString(KEY_WEATHER_DATA_RESULT);
                        Log.d(MAIN_ACTIVITY_LOG_TAG,
                                "Serialized object string: " + serializedWeatherData);
                        if (serializedWeatherData != null) {
                            WeatherData weatherData = TaskObjectSerializationHelper
                                    .weatherDataDeserializeFromJson(serializedWeatherData);

                            // display the current weather conditions to the UI
                            TextView humidityText = findViewById(R.id.humidity_textview);
                            humidityText.setText(weatherData.getWeatherHumidity() + "%");
                            // max level between tree and grass pollen
                            TextView pollen = findViewById(R.id.pollen_textview);
                            Level maxPollen =
                                    (weatherData.getWeatherGrassIndex().ordinal() >
                                            weatherData.getWeatherTreeIndex().ordinal()) ?
                                            (weatherData.getWeatherGrassIndex()) :
                                            (weatherData.getWeatherTreeIndex());
                            pollen.setText(levelValuesToText[maxPollen.ordinal()]);
                            // precipitation (mm/hr)
                            TextView precipitationText = findViewById(R.id.precipitation_textview);
                            precipitationText
                                    .setText(weatherData.getWeatherPrecipitationIntensity() + " mm/hr");
                            Log.d(MAIN_ACTIVITY_LOG_TAG, "Set precipitation to -> " + precipitationText.getText());
                            // temperature in Celsius
                            TextView temperatureText = findViewById(R.id.temperature_textview);
                            temperatureText.setText(weatherData.getWeatherTemperature() + " C");
                            // AQI(a.k.a. EPA Index)
                            TextView aqiText = findViewById(R.id.aqi_textview);
                            aqiText.setText("Test");
                            Log.d(MAIN_ACTIVITY_LOG_TAG, "AQI was -> " + weatherData.getWeatherEPAIndex());
                            // Note: setText() fails if the input is not a String (a plain old int
                            // or something)
                            aqiText.setText("Index : "
                                    + Integer.toString(weatherData.getWeatherEPAIndex()));
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