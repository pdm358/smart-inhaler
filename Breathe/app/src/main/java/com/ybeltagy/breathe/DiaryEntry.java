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
 * Encapsulates user-added information for a given IUE - comments and tags
 * - One IUE object contains one DiaryEntry object
 *
 * TODO: write static DiaryEntry createDiaryEntry() to create a DiaryEntry object
 */
public class DiaryEntry {
    private List<Tag> tags; // categories this IUE falls into, as defined by the user
    private String message; // user entered message describing the IUE

    public DiaryEntry(List<Tag> tags, String message) {
        this.tags = tags;
        this.message = message;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
