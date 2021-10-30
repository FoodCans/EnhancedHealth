package dev.foodcans.enhancedhealth.storage;

import dev.foodcans.enhancedhealth.data.HealthData;
import dev.foodcans.pluginutils.Callback;

import java.util.Set;
import java.util.UUID;

public interface IStorage
{
    void loadStorage(UUID uuid, Callback<HealthData> callback);

    void saveStorage(HealthData healthData);

    HealthData loadData(UUID uuid);

    void saveData(HealthData healthData);

    void getAllData(Callback<Set<HealthData>> callback);

    void deleteStorage();
}
