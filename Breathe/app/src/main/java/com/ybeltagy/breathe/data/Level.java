package com.ybeltagy.breathe.data;

/**
 * 6 state level (used for pollen indexes) + 1 state for NULL
 */
public enum Level {
    NONE,
    VERY_LOW,
    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH,
    // NULL is last to simplify things.  tomorrow.io returns 0 for None, so this is last so we can
    // use the ordinal values of Level for enum evaluation
    NULL;

    /**
     * Returns the max Level of 2, assuming Level enums are defined in ascending order
     * @param first the first Level
     * @param second the second Level
     * @return the higher ordinal value Level
     */
    public static Level maxLevelOfTwo(Level first, Level second) {
        if (first == Level.NULL && second == Level.NULL) {
            return Level.NULL;
        }
        else if (second == Level.NULL) {
            return first;
        }
        else if (first == Level.NULL) {
            return second;
        }
        return first.ordinal() > second.ordinal() ? first : second;
    }

    /**
     * This methods Level is dependent on the ordering in the enum:
     * {NONE, VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH, NULL}
     * https://docs.tomorrow.io/reference/data-layers-pollen
     * @param numToConvert
     * @return
     */
    public static Level intToLevel(int numToConvert) {
        return Level.values()[numToConvert];
    }
}
