package dev.foodcans.enhancedhealth.storage;

import com.google.gson.Gson;
import dev.foodcans.enhancedhealth.EnhancedHealth;
import dev.foodcans.enhancedhealth.data.HealthData;
import dev.foodcans.pluginutils.Callback;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JsonStorage implements IStorage
{
    private final File playersFolder;
    private final Gson gson = new Gson();

    public JsonStorage(File playersFolder)
    {
        this.playersFolder = playersFolder;
        if (!this.playersFolder.exists())
        {
            this.playersFolder.mkdir();
        }
    }

    @Override
    public void loadStorage(UUID uuid, Callback<HealthData> callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(EnhancedHealth.getInstance(), () ->
        {
            HealthData healthData = loadData(uuid);
            Bukkit.getScheduler().runTask(EnhancedHealth.getInstance(), () -> callback.call(healthData));
        });
    }

    @Override
    public void saveStorage(HealthData healthData)
    {
        Bukkit.getScheduler()
                .runTaskAsynchronously(EnhancedHealth.getInstance(), () -> saveData(healthData));
    }

    @Override
    public HealthData loadData(UUID uuid)
    {
        Path path = Paths.get(playersFolder.getAbsolutePath(), uuid.toString() + ".json");
        if (!path.toFile().exists())
        {
            saveData(new HealthData(uuid));
        }
        try (Reader reader = Files.newBufferedReader(path))
        {
            return gson.fromJson(reader, HealthData.class);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveData(HealthData healthData)
    {
        Path path = Paths.get(playersFolder.getAbsolutePath(),
                healthData.getUuid().toString() + ".json");
        if (!path.toFile().exists())
        {
            try
            {
                path.toFile().createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try (Writer writer = Files.newBufferedWriter(path))
        {
            gson.toJson(healthData, writer);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void getAllData(Callback<Set<HealthData>> callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(EnhancedHealth.getInstance(), () ->
        {
            Set<HealthData> data = new HashSet<>();
            for (File file : playersFolder.listFiles())
            {
                if (file.isFile())
                {
                    data.add(loadData(UUID.fromString(file.getName().replace(".json", ""))));
                }
            }
            Bukkit.getScheduler().runTask(EnhancedHealth.getInstance(), () -> callback.call(data));
        });
    }
}
