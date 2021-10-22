package dev.foodcans.enhancedhealth.storage;

import dev.foodcans.enhancedhealth.data.HealthData;
import dev.foodcans.enhancedhealth.util.Callback;

import java.util.UUID;

public class MySQLStorage implements IStorage
{
    @Override
    public void loadStorage(UUID uuid, Callback<HealthData> callback)
    {

    }

    @Override
    public void saveStorage(HealthData healthData)
    {

    }

    @Override
    public HealthData loadData(UUID uuid)
    {
        return null;
    }

    @Override
    public void saveData(HealthData healthData)
    {

    }
}
