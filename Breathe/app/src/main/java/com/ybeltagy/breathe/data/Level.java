package com.ybeltagy.breathe.data;

/**
 * 6 state level (used for pollen indexes) + 1 state for NULL
 */
public enum Level {
    NULL,
    NONE,
    VERY_LOW,
    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH;

    /**
     * Returns the max Level of 2, assuming Level enums are defined in ascending order
     * @param first the first Level
     * @param second the second Level
     * @return the higher ordinal value Level
     */
    public static Level maxLevelOfTwo(Level first, Level second) {
        return first.ordinal() > second.ordinal() ? first : second;
    }
}
