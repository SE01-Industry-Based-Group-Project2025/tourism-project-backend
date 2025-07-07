package com.sl_tourpal.backend.service;

import com.sl_tourpal.backend.domain.Activity;
import com.sl_tourpal.backend.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;



@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public Activity updateActivity(Long id, Activity updatedActivity) {
        Activity existing = activityRepository.findById(id).orElseThrow();
        existing.setName(updatedActivity.getName());
        existing.setRegion(updatedActivity.getRegion());
        return activityRepository.save(existing);
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public List<Activity> getActivitiesByRegions(List<String> regions) {
        return activityRepository.findByRegionIn(regions);
    }
}
