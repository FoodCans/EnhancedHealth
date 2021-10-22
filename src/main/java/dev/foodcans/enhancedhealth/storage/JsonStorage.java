package dev.foodcans.enhancedhealth.storage;

import com.google.gson.Gson;
import dev.foodcans.enhancedhealth.EnhancedHealth;
import dev.foodcans.enhancedhealth.data.HealthData;
import dev.foodcans.enhancedhealth.util.Callback;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            Bukkit.broadcastMessage(ChatColor.GREEN + "Data loaded for player: " + uuid);
        });
    }

    @Override
    public void saveStorage(HealthData healthData)
    {
        Bukkit.getScheduler().runTaskAsynchronously(EnhancedHealth.getInstance(), () -> saveData(healthData));
        Bukkit.broadcastMessage(ChatColor.GREEN + "Data saved for player: " + healthData.getUuid());
    }

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

    public void saveData(HealthData healthData)
    {
        Path path = Paths.get(playersFolder.getAbsolutePath(), healthData.getUuid().toString() + ".json");
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
}
