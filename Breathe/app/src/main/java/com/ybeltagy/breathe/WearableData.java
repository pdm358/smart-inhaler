package com.ybeltagy.breathe;

/**
 * Encapsulates local environmental data gathered from the user's smart pin/wearable
 * - currently only contains one actual field of data (temperature)
 * - the other placeholder fields will be used by future teams when additional sensors are added to
 * to the smart pin/wearable
 * <p>
 * TODO: write static WearableData createWearableData() to collect smart pin data
 * and create a WearableData object
 */
public class WearableData {
    private int temperature;
    private int placeHolder1;
    private int placeHolder2;
    private int placeHolder3;


    public WearableData(int temperature, int placeHolder1, int placeHolder2, int placeHolder3) {
        this.temperature = temperature;
        this.placeHolder1 = placeHolder1;
        this.placeHolder2 = placeHolder2;
        this.placeHolder3 = placeHolder3;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getPlaceHolder1() {
        return placeHolder1;
    }

    public void setPlaceHolder1(int placeHolder1) {
        this.placeHolder1 = placeHolder1;
    }

    public int getPlaceHolder2() {
        return placeHolder2;
    }

    public void setPlaceHolder2(int placeHolder2) {
        this.placeHolder2 = placeHolder2;
    }

    public int getPlaceHolder3() {
        return placeHolder3;
    }

    public void setPlaceHolder3(int placeHolder3) {
        this.placeHolder3 = placeHolder3;
    }
}
