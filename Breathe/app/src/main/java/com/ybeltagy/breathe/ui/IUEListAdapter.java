package com.ybeltagy.breathe.ui;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ybeltagy.breathe.R;
import com.ybeltagy.breathe.data.DataFinals;
import com.ybeltagy.breathe.data.InhalerUsageEvent;
import com.ybeltagy.breathe.data.WearableData;
import com.ybeltagy.breathe.data.WeatherData;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

/**
 * This class prepares and updates the IUE data to be displayed in the RecyclerView in the Main
 * Activity.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class IUEListAdapter
        extends RecyclerView.Adapter<IUEListAdapter.IUEViewHolder> {

    protected final static DateTimeFormatter formatter =
            DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                    .withLocale( Locale.US )
                    .withZone( ZoneId.systemDefault() );

    // Cached copy of InhalerUsageEvents
    private List<InhalerUsageEvent> iueEntries;

    // Inflater
    private final LayoutInflater iueInflater;

    // Tells the recyclerview which item was clicked
    private static IUEListItemClickListener iueListItemClickListener;

    protected interface IUEListItemClickListener {
        void onItemClick(View v, int position);
    }

    protected IUEListAdapter(Context context) {
        iueInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public IUEViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = iueInflater.inflate(R.layout.iue_item, parent, false); // what happens if attachToRoot is true
        return new IUEViewHolder(itemView);
    }

    /**
     * I tested the database when it was empty. It simply didn't display anything. The case in which
     * iueEntries is null is not possible. Why is there an if statement?
     * @param holder
     * @param position
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull IUEViewHolder holder, int position) {

        // Get the the iue
        InhalerUsageEvent current = iueEntries.get(position);

        Log.d("IUEListAdapter", "setting text to "
                + current.getInhalerUsageEventTimeStamp().toString());

        // Display the iue.
        holder.setIUE(current);
    }

    public void setIUEs(List<InhalerUsageEvent> inhalerUsageEvents) {
        iueEntries = inhalerUsageEvents;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return iueEntries != null ? iueEntries.size() : 0;
    }

    /**
     * Identifies which InhalerUsageEvent was clicked for methods that handle user events
     *
     * @param position Position of InhalerUsageEvent in RecyclerView
     * @return The InhalerUsageEvent at the input position
     */
    protected InhalerUsageEvent getInhalerUsageEventAtPosition(int position) {
        return iueEntries.get(position);
    }

    protected void setOnItemClickListener(IUEListItemClickListener IUEListItemClickListener) {
        iueListItemClickListener = IUEListItemClickListener;
    }

    // Class that holds View information for displaying one item from the item's layout
    static class IUEViewHolder extends RecyclerView.ViewHolder {

        public final LinearLayout inhalerUsageEventEntry; // the entire entry in the timeline

        public final TextView iueTimeStamp;  // text view of the timestamp

        // Diary
        public final TextView diaryMessage; // text view of the diary entry
        public final TextView tag;          // text view of the tag

        // Wearable Data
        public final TextView wearableLabel;
        public final TextView wearableTemperature;
        public final TextView wearableHumidity;
        public final TextView wearableCharacter;
        public final TextView wearableDigit;
        public final TextView wearablePm;

        // Weather Data
        public final TextView weatherLabel;
        public final TextView weatherTemperature;
        public final TextView weatherHumidity;
        public final TextView weatherPrecipitationIntensity;
        public final TextView weatherTreePollen;
        public final TextView weatherGrassPollen;
        public final TextView weatherEPAIndex;


        public IUEViewHolder(@NonNull View itemView) {
            super(itemView);
            inhalerUsageEventEntry = itemView.findViewById(R.id.inhaler_usage_event_entry);

            inhalerUsageEventEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iueListItemClickListener.onItemClick(view, getAbsoluteAdapterPosition());
                }
            });

            // Timestamp textview
            iueTimeStamp = itemView.findViewById(R.id.iue_entry_timestamp_textview);

            // Diary Textviews
            diaryMessage = itemView.findViewById(R.id.iue_entry_diary_textview);
            tag = itemView.findViewById(R.id.iue_entry_tag_textview);

            // WearableData Textviews
            wearableLabel = itemView.findViewById(R.id.wearable_label_textview);
            wearableTemperature = itemView.findViewById(R.id.wearable_temp_textview);
            wearableHumidity = itemView.findViewById(R.id.wearable_humid_textview);
            wearableCharacter = itemView.findViewById(R.id.wearable_char_textview);
            wearableDigit = itemView.findViewById(R.id.wearable_digit_textview);
            wearablePm = itemView.findViewById(R.id.wearable_pm_textview);

            // WeatherData Textviews
            weatherLabel = itemView.findViewById(R.id.weather_label_textview);
            weatherTemperature = itemView.findViewById(R.id.weather_temp_textview);
            weatherHumidity = itemView.findViewById(R.id.weather_humid_textview);
            weatherPrecipitationIntensity = itemView.findViewById(R.id.weather_precipitation_textview);
            weatherTreePollen = itemView.findViewById(R.id.weather_tree_pollen_textview);
            weatherGrassPollen = itemView.findViewById(R.id.weather_grass_pollen_textview);
            weatherEPAIndex = itemView.findViewById(R.id.weather_aqi_textview);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        protected void setIUE(InhalerUsageEvent current){

            // Clear the TextViews to avoid getting artifacts.
            clearTextViews();

            // timestamp
            iueTimeStamp.setText(formatter.format(current.getInhalerUsageEventTimeStamp()));

            // show tag
            if (current.getDiaryEntry().getTag() != DataFinals.DEFAULT_TAG) { // only print user-chosen tags
                tag.setText(current.getDiaryEntry().getTag().toString());
            }

            // Diary entry
            diaryMessage.setText(current.getDiaryEntry().getMessage());

            // Wearable Data
            {
                WearableData wearableData = current.getWearableData();
                wearableLabel.setText("Pin\nData:");
                if(wearableData.isTemperatureValid()) wearableTemperature.setText(String.format(Locale.US, "T:\n%.0f°C", wearableData.getTemperature()));
                if(wearableData.isHumidityValid()) wearableHumidity.setText(String.format(Locale.US,"H:\n%.0f %%", wearableData.getHumidity()));
                if(wearableData.isPm_countValid()) wearablePm.setText(String.format(Locale.US,"PM:\n %d", wearableData.getPm_count()));
                if(wearableData.isCharacterValid()) wearableCharacter.setText(String.format("C:\n%c", wearableData.getCharacter()));
                if(wearableData.isDigitValid()) wearableDigit.setText(String.format("D:\n%c", wearableData.getDigit()));


            }

            // Weather Data
            {
                WeatherData weatherData = current.getWeatherData();
                weatherLabel.setText("Weather\nData:");
                if(weatherData.isWeatherTemperatureValid()) weatherTemperature.setText(String.format(Locale.US, "T:\n%.0f°C", weatherData.getWeatherTemperature()));
                if(weatherData.isWeatherHumidityValid()) weatherHumidity.setText(String.format(Locale.US,"H:\n%.0f", weatherData.getWeatherHumidity()));
                if(weatherData.isWeatherPrecipitationIntensityValid()) weatherPrecipitationIntensity.setText(String.format(Locale.US,"P:\n%.0f", weatherData.getWeatherPrecipitationIntensity()));
                if(weatherData.isWeatherTreeIndexValid()) weatherTreePollen.setText(String.format(Locale.US, "Tree Pollen\n%s", weatherData.getWeatherTreeIndex().toString()));
                if(weatherData.isWeatherGrassIndexValid()) weatherGrassPollen.setText(String.format(Locale.US, "Grass Pollen:\n%s", weatherData.getWeatherGrassIndex().toString()));
                if(weatherData.isWeatherEPAIndexValid()) weatherEPAIndex.setText(String.format(Locale.US,"AQI:\n%d", weatherData.getWeatherEPAIndex()));
            }
        }

        /**
         * Empties all the TextViews
         */
        private void clearTextViews(){
            iueTimeStamp.setText("");

            tag.setText("");
            diaryMessage.setText("");

            wearableLabel.setText("");
            wearableTemperature.setText("");
            wearableHumidity.setText("");
            wearableCharacter.setText("");
            wearableDigit.setText("");
            wearablePm.setText("");

            weatherLabel.setText("");
            weatherTemperature.setText("");
            weatherHumidity.setText("");
            weatherPrecipitationIntensity.setText("");
            weatherTreePollen.setText("");
            weatherGrassPollen.setText("");
            weatherEPAIndex.setText("");
        }
    }
}
