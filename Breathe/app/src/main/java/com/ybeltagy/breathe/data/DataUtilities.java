package com.ybeltagy.breathe.data;

import android.annotation.SuppressLint;

// TODO: write unit tests
public class DataUtilities {

    /**
     * The column names of the IUEData. This is used in the feature to export IUE data to a csv file.
     * The order of items must be maintained when inserting an IUE into the CSV file.
     */
    private static final String[] InhalerUsageEventsTableColumnNames = new String[]{
            "IUE Timestamp",

            "Diary Tag",
            "Diary Message",

            // ignores the wearable data timestamp
            "Wearable Temperature",
            "Wearable Humidity",
            "Wearable Character",
            "Wearable Digit",

            "Weather Temperature",
            "Weather Humidity",
            "Weather Grass Index",
            "Weather Tree Index",
            "Weather AQI"
    };

    public static void addIUETableColumnNames(StringBuilder sb){
        for(int i = 0; i < InhalerUsageEventsTableColumnNames.length; i++){
            sb.append(InhalerUsageEventsTableColumnNames[i]);

            if(i + 1 < InhalerUsageEventsTableColumnNames.length){
                sb.append(",");
            }

        }
        sb.append('\n'); // end line
    }

    /**
     * Writes the iue to the string builder but ignores the wearable data timestamp.
     * @param sb
     * @param iue
     * @see <a href=https://techterms.com/definition/csv>csv</a>
     */
    @SuppressLint("NewApi")
    public static void appendIUE(StringBuilder sb, InhalerUsageEvent iue) {

        char comma = ',';

        sb.append(iue.getInhalerUsageEventTimeStamp().toString());
        sb.append(comma);

        /**
         *  Diary Entry
         */
        {
            DiaryEntry diaryEntry = iue.getDiaryEntry();
            if (diaryEntry.isTagValid())
                sb.append(diaryEntry.getTag().toString());
            sb.append(comma);

            sb.append('\"'); // always append strings with quotation marks so commas and CR/LF characters don't ruin the formatting.
            sb.append(diaryEntry.getMessage().replace("\"","\"\"")); // always append the message and replace double quotes with two double quotes.
            sb.append('\"');
            sb.append(comma);
        }

        /**
         * WearableData
         */
        {
            WearableData wearableData = iue.getWearableData();
            if (wearableData.isTemperatureValid())
                sb.append(wearableData.getTemperature());
            sb.append(comma);

            if (wearableData.isHumidityValid())
                sb.append(wearableData.getHumidity());
            sb.append(comma);

            if (wearableData.isCharacterValid())
                sb.append(wearableData.getCharacter());
            sb.append(comma);

            if (wearableData.isDigitValid())
                sb.append(wearableData.getDigit());
            sb.append(comma);

        }

        /**
         * WeatherData
         */
        {
            WeatherData weatherData = iue.getWeatherData();
            if (weatherData.isWeatherTemperatureValid())
                sb.append(weatherData.getWeatherTemperature());
            sb.append(comma);

            if (weatherData.isWeatherHumidityValid())
                sb.append(weatherData.getWeatherHumidity());
            sb.append(comma);

            if (weatherData.isWeatherPollenValid())
                sb.append(weatherData.getWeatherGrassIndex().toString());
            sb.append(comma);

            if (weatherData.isWeatherPollenValid())
                sb.append(weatherData.getWeatherTreeIndex().toString());
            sb.append(comma);

            if (weatherData.isWeatherEPAIndexValid())
                sb.append(weatherData.getWeatherEPAIndex());
            // There is no need to add a comma since this is the last item in the inhaler usage event.
        }

        sb.append('\n'); // each line represents an IUE, so end the line.

    }

    /**
     * If num is NaN, returns DataFinals.DEFAULT_FLOAT. Otherwise, returns num.
     * @param num the num to be checked
     * @return num if num is not Nan; DataFinals.DEFAULT_FLOAT otherwise.
     */
    protected static float nanGuard(float num){
        if(num == Float.NaN) return DataFinals.DEFAULT_FLOAT;

        return num;
    }

}
