package com.ybeltagy.breathe;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    // Todo:: decide if we should dete this or not - we should probably never use this
    public void update(InhalerUsageEvent inhalerUsageEvent) {
        breatheRepository.update(inhalerUsageEvent);
    }

    public void updateDiaryEntry(Instant timeStamp, Tag tag, String diaryMessage) {
        breatheRepository.updateDiaryEntry(timeStamp,tag, diaryMessage);
    }

    public void updateWearableData(Instant timeStamp, float temperature, float humidity, char character, char digit) {
        breatheRepository.updateWearableData(timeStamp, temperature, humidity, character, digit);
    }

    /**
     * Todo: fix - this needs to use the ExectorService; it crashes the UI
     * @param timeStamp
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<InhalerUsageEvent> getInhalerUsageEventWithTimeStamp(Instant timeStamp) throws ExecutionException, InterruptedException {
        return breatheRepository.getInhalerUsageEventWithTimeStamp(timeStamp);
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
