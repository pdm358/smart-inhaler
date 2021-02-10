package com.ybeltagy.breathe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textview);

        //fixme: find a better place to do this.
        WeatherData.apiKey = getString(R.string.clima_cell_api_key);

        (new Thread() {
            public void run() {
                WeatherData.syncGetWeatherData(Calendar.getInstance(), 47.6062,-122.3321);
            }
        }).start();
    }
}