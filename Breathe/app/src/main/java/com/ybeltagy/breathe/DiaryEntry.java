package com.ybeltagy.breathe;

import java.util.List;

/**
 * Categories of IUE's, as annotated by the user
 */
enum Tag {
    PREVENTATIVE, // tag used proactively before any symptoms are experienced
    RESCUE  // tag used in response to an asthma attack or problematic symptoms
}

/**
 * Encapsulates user-added information for a given IUE - comments and tag
 * - One IUE object contains one DiaryEntry object
 * <p>
 * TODO: write static DiaryEntry createDiaryEntry() to create a DiaryEntry object
 */
public class DiaryEntry {
    // TODO: change to list of tags and add converter for database
    private Tag tag; // category this IUE falls into, as defined by the user
    private String message; // user entered message describing the IUE

    public DiaryEntry(Tag tag, String message) {
        this.tag = tag;
        this.message = message;
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
