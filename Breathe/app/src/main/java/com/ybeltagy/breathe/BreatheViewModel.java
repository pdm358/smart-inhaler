package com.ybeltagy.breathe;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.time.Instant;
import java.util.List;

/**
 * Class whose role is to act as a communication center between the Repository and the UI; provides
 * data and survives configuration changes
 */
public class BreatheViewModel extends AndroidViewModel {
    private final BreatheRepository breatheRepository;
    private final LiveData<List<InhalerUsageEvent>> allInhalerUsageEvents;


    public BreatheViewModel(@NonNull Application application) {
        super(application);
        breatheRepository = new BreatheRepository(application);
        allInhalerUsageEvents = breatheRepository.getAllInhalerUsageEvents();
    }

    LiveData<List<InhalerUsageEvent>> getAllInhalerUsageEvents() {
        return allInhalerUsageEvents;
    }

    public void insert(InhalerUsageEvent inhalerUsageEvent) {
        breatheRepository.insert(inhalerUsageEvent);
    }

    /**
     * TODO: maybe we should never use this because it "clobbers" our existing IUEs (unless we
     *      can also retrieve the existing inhalerUsageEvent, update the data and use the same
     *      inhalerUsageEvent object as the input to this function) (might want to delete it)
     *      <p>
     * @param inhalerUsageEvent
     */
    public void update(InhalerUsageEvent inhalerUsageEvent) {
        breatheRepository.update(inhalerUsageEvent);
    }

    public void updateDiaryEntry(Instant timeStamp, Tag tag, String diaryMessage) {
        breatheRepository.updateDiaryEntry(timeStamp,tag, diaryMessage);
    }

    public void updateWearableData(Instant inhalerUsageTimestamp, Instant wearableDataTimestamp,
                                   float temperature, float humidity, char character, char digit) {
        breatheRepository.updateWearableData(
                inhalerUsageTimestamp, wearableDataTimestamp, temperature, humidity,
                character, digit);
    }

    /**
     * IMPORTANT : This is for testing purposes ONLY -> to clear away placeholder IUEs we've created
     * as we develop and test the app.
     * <p>
     * TODO: Create button in UI Main Activity to use this function and delete all
     *        placeholderIUEs so we can clearly test different behaviours in the future
     */
    public void deleteAll() {
        breatheRepository.deleteAllInhalerUsageEvents();
    }
}
