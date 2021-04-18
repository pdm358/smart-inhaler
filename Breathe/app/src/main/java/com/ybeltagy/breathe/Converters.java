package com.ybeltagy.breathe;

import android.os.Build;

import androidx.room.TypeConverter;

import java.time.Instant;

/**
 * Class for converting more complicated POJOs into types that can be stored in Room
 */
public class Converters {

    /**
     * Converts database stored timeStamp string into relevant Instant (UTC)
     *
     * @param timeStamp
     * @return parsed Instant
     */
    @TypeConverter
    public static Instant fromTimeStampString(String timeStamp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return timeStamp == null ? null : Instant.parse(timeStamp);
        } else {
            // TODO: implement date time formatting from string to Instant.
            //  for API < 26 (Oreo)
            // FIXME: This is really important.
            return null;
        }
    }

    /**
     * Converts Instant timeStamp to string to be stored in database
     *
     * @param timeStamp
     * @return timestamp as a string
     */
    @TypeConverter
    public static String toTimeStampString(Instant timeStamp) {
        return timeStamp == null ? null : timeStamp.toString();
    }

    //  Converts Tag int stored in database to relevant Tag enum
    @TypeConverter
    public static Tag fromTagIntValue(int tagValue) {
        return Tag.values()[tagValue];
    }

    // Converts Tag into int to be stored in database
    @TypeConverter
    public static int toTagIntValue(Tag tag) {
        return tag != null ? tag.ordinal() : Tag.NULL.ordinal();
    }

    // Converts Level int stored in database to relevant Level enum
    @TypeConverter
    public static Level fromLevelIntValue(int level) {
        return Level.values()[level];
    }

    // Converts Level into int to be stored in database
    @TypeConverter
    public static int toLevelIntValue(Level level) {
        return level != null ? level.ordinal() : Level.NULL.ordinal();
    }
}
