package com.ybeltagy.breathe.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.os.Bundle;
import android.widget.Toast;

import com.ybeltagy.breathe.ble.BLEScanner;
import com.ybeltagy.breathe.ble.BLEService;
import com.ybeltagy.breathe.data.InhalerUsageEvent;
import com.ybeltagy.breathe.R;
import com.ybeltagy.breathe.data.WearableData;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

/**
 * This activity contains the main logic of the Breathe app. It renders the UI and registers a
 * Bluetooth Broadcast receiver to listen for Bluetooth connection and disconnection to the phone
 * (to be changed to BLE).
 */
public class MainActivity extends AppCompatActivity {

    // MainActivity's interactions with the data layer are through BreatheViewModel alone
    private BreatheViewModel breatheViewModel; // view model for the recyclerview

    private static final String tag = MainActivity.class.getName(); // Maybe we can use this, going forward.

    private static final int ACCESS_FINE_LOCATION_REQUEST = 1;
    private static final int ENABLE_BLUETOOTH = 2; // todo: consider moving to a centralized location

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fixme: is there a better way to detect the app was opened?
        Intent foregroundServiceIntent = new Intent(this, BLEService.class);
        startForegroundService(foregroundServiceIntent); //FIXME why does it take a context too?

        // Note: constructor in code lab did not work; searched for a long time and this fixed it:
        // https://github.com/googlecodelabs/android-room-with-a-view/issues/145#issuecomment-739756244
        breatheViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(BreatheViewModel.class);

        // ProgressBar
        renderMedStatusView(UIFinals.TOTAL_DOSES_IN_CANISTER);

        // RecyclerView
        // populate the fake data for the RecyclerView using
        renderDiaryView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * render the Progress Bar for the medicine status in first pane (top of the screen)
     * @param totalDosesInCanister the size of the inhaler canister
     */
    private void renderMedStatusView(int totalDosesInCanister) {
        ProgressBar medicineStatusBar = findViewById(R.id.doses_progressbar);

        // update max amount of progress bar to number of doses in a full medicine canister
        medicineStatusBar.setMax(totalDosesInCanister);

        //Get text view to set the text for.
        TextView dosesTakenText = findViewById(R.id.doses_textview);

        breatheViewModel.getAllInhalerUsageEvents().observe(this, inhalerUsageEvents -> {
            // medicine left is number of doses in a full container - doses used
            medicineStatusBar.setProgress(medicineStatusBar.getMax() - inhalerUsageEvents.size());
            dosesTakenText.setText(String.format(Locale.ENGLISH,"%d / %d", medicineStatusBar.getProgress(), medicineStatusBar.getMax()));
        });
    }

    /**
     * render the diary RecyclerView for the diary timeline of events in third pane
     */
    @SuppressLint("NewApi")
    private void renderDiaryView() {

        Log.d(tag, "DiaryView starting to render...");

        RecyclerView iueRecyclerView = findViewById(R.id.diary_recyclerview);

        IUEListAdapter iueListAdapter = new IUEListAdapter(this); // make adapter and provide data to be displayed

        // set an on-click listener so we can get the InhalerUsageEvent at the clicked position and
        // pass it to the DiaryEntryActivity
        iueListAdapter.setOnItemClickListener((v, position) -> launchDiaryEntryActivity(iueListAdapter.getInhalerUsageEventAtPosition(position)));

        iueRecyclerView.setAdapter(iueListAdapter); // connect adapter and recyclerView

        // set layout manager for recyclerView
        iueRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Updated cached copy of InhalerUsageEvents
        breatheViewModel.getAllInhalerUsageEvents().observe(this, inhalerUsageEvents -> {
            // update all the dataset of the recycler view for any change in the inhaler usage events.
            // I'm not sure this is the most efficient way to do it, but it is guaranteed to work.
            iueListAdapter.setIUEs(inhalerUsageEvents);
            Log.d(tag, "database changed - added IUEs");
        });
    }

    /**
     * Opens the diary entry activity for the passed inhalerUsageEvent.
     * todo: as the project develops, consider not not passing anymore than than IUE timestamp in the intent and retrieving
     *  everything from the database in the DiaryEntryActivity
     * @param inhalerUsageEvent the iue to edit the diary for
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void launchDiaryEntryActivity(InhalerUsageEvent inhalerUsageEvent) {

        // don't do anything for invalid input.
        if(inhalerUsageEvent == null || inhalerUsageEvent.getInhalerUsageEventTimeStamp() == null) return;

        Log.d(tag, "Launching diary entry activity!");

        Intent intent = new Intent(this, DiaryEntryActivity.class);

        // send timestamp
        intent.putExtra(UIFinals.EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TIMESTAMP_KEY,
                inhalerUsageEvent.getInhalerUsageEventTimeStamp().toString());

        intent.putExtra(UIFinals.EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_EXISTING_MESSAGE,
                inhalerUsageEvent.getDiaryEntry().getMessage());

        intent.putExtra(UIFinals.EXTRA_DATA_UPDATE_INHALER_USAGE_EVENT_TAG,
                inhalerUsageEvent.getDiaryEntry().getTag());

        startActivityForResult(intent, UIFinals.UPDATE_INHALER_USAGE_EVENT_REQUEST_CODE);

    }

    /**
     * Starts a scan for a wearable sensor device. The wearable includes its service UUID in its advertisements.
     * The service UUID of the wearable sensor is used to filter for it.
     * @param view
     */
    public void onScanButtonClick(View view) {
        scanForWearableSensor();
    }

    public void scanForWearableSensor(){
        if(!hasLocationPermissions()) return;

        if(!isBluetoothEnabled()) return;

        BLEScanner.scanForWearableSensor(this);
    }

    /**
     * Returns true if the app has ACCESS_FINE_LOCATION permission. If not, it requests it from the user and returns false.
     * @return true if app has ACCESS_FINE_LOCATION permission
     */
    private boolean hasLocationPermissions() {
        // M is for Marshmallow. Before Android Marshmallow, permissions are given at install time rather than at runtime.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)  return true;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "Bluetooth scanning requires location permission", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, ACCESS_FINE_LOCATION_REQUEST);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == ACCESS_FINE_LOCATION_REQUEST){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                scanForWearableSensor();
            }else{
                Toast.makeText(this, "Can't scan without a Location permissions", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    // todo: implement a way an on request result that will restart the scan.
    /**
     * Returns true if Bluetooth is enabled. Otherwise, requests the user to enable it and returns false.
     * @return true if Bluetooth is enabled.
     */
    private boolean isBluetoothEnabled(){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // There is no Bluetooth adapter (i.e, the device doesn't support bluetooth)
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "This device does not support bluetooth", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Bluetooth is supported and enabled.
        if(bluetoothAdapter.isEnabled()) {
            return true;
        }

        // Bluetooth is supported but not enabled.
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        Toast.makeText(this, "Please enable Bluetooth", Toast.LENGTH_SHORT).show();
        startActivityForResult(enableBtIntent, 0);

        return false;
    }


    /**
     * A testing method just for development.
     * @param view
     */
    public void testWearableData(View view) {
        //Fixme: inline thread is just a demo. Remove and use executorService later.
        //Context context = this;

        (new Thread() {
            public void run() {
                WearableData wearableData = BLEService.getWearableData();
                String temp = "No data";

                if(wearableData != null) temp = "Wearable Data!" +
                        "\nTemp = " + wearableData.getTemperature() + "\nHumidity: " + wearableData.getHumidity() +
                        "\nCharacter: " + wearableData.getCharacter() + "\nDigit: " + wearableData.getDigit();

                Log.d(tag,temp);
            }
        }).start();
    }

    public void onSimulateIUEButtonClick(View view) {
        breatheViewModel.simulateIUE(this);
    }

    public void export(View view){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 10; i++){
            sb.append(i);
            sb.append(",");
        }

        // todo: try with resource
        // todo: consider making asynchronous
        try{
            FileOutputStream out = openFileOutput("iue_data.csv",Context.MODE_PRIVATE);
            out.write(sb.toString().getBytes());
            out.close();

            Context context = this;
            File file = new File(getFilesDir(), "iue_data.csv");
            Uri path = FileProvider.getUriForFile(context, "com.ybeltagy.breathe.fileprovider", file); // getUriForFile(context, "${context.packageName}.fileprovider", file)
            Intent fileIntent = new Intent(Intent.ACTION_SEND); // todo: consider using view but with the correct mime type.
            //context.contentResolver.getType(contentUri)
            //    intent.setDataAndType(contentUri, mimeType)
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT,"IUE Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM,path);
            startActivity(Intent.createChooser(fileIntent, "Export IUE"));
        }catch (Exception e){
            Log.d(tag,e.toString());
        }
    }
}