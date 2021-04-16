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

    public void update(InhalerUsageEvent inhalerUsageEvent) {
        breatheRepository.update(inhalerUsageEvent);
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
