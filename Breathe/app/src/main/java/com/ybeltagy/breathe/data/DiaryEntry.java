package com.ybeltagy.breathe.data;

import androidx.room.Ignore;

/**
 * Encapsulates user-added information for a given InhalerUsageEvent - comments and tag
 * - One InhalerUsageEvent object contains one DiaryEntry object
 * <p>
 *
 */
public class DiaryEntry {
    private Tag tag; // category this InhalerUsageEvent falls into, as defined by the user
    private String message; // user entered message describing the InhalerUsageEvent

    @Ignore
    public DiaryEntry(Tag tag, String message) {
        this.tag = tag;
        this.message = message;
    }

    public DiaryEntry() {
        this(DataFinals.DEFAULT_TAG, DataFinals.DEFAULT_STRING);
    }

    public boolean isTagValid(){
        return tag != DataFinals.DEFAULT_TAG;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
