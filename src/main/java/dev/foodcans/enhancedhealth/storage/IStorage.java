package dev.foodcans.enhancedhealth.storage;

import dev.foodcans.enhancedhealth.data.HealthData;
import dev.foodcans.enhancedhealth.util.Callback;

import java.util.UUID;

public interface IStorage
{
    void loadStorage(UUID uuid, Callback<HealthData> callback);

    void saveStorage(HealthData healthData);

    HealthData loadData(UUID uuid);

    void saveData(HealthData healthData);
}
