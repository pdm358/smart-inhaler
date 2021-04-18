package com.ybeltagy.breathe;

/**
 * Categories of InhalerUsageEvent's, as annotated by the user
 */
enum Tag {
    NULL, // tag that has not been initialized
    PREVENTATIVE, // tag used proactively before any symptoms are experienced
    RESCUE  // tag used in response to an asthma attack or problematic symptoms
}

/**
 * Encapsulates user-added information for a given InhalerUsageEvent - comments and tag
 * - One InhalerUsageEvent object contains one DiaryEntry object
 * <p>
 *
 */
public class DiaryEntry {
    private Tag tag; // category this InhalerUsageEvent falls into, as defined by the user
    private String message; // user entered message describing the InhalerUsageEvent

    public DiaryEntry(Tag tag, String message) {
        this.tag = tag;
        this.message = message;
    }

    public DiaryEntry() {
        setTag(Tag.NULL);
        setMessage("");
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
