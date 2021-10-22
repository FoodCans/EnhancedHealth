package dev.foodcans.enhancedhealth.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.foodcans.enhancedhealth.EnhancedHealth;
import dev.foodcans.enhancedhealth.data.HealthData;
import dev.foodcans.enhancedhealth.settings.Config;
import dev.foodcans.enhancedhealth.util.Callback;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MySQLStorage implements IStorage
{
    private HikariDataSource dataSource;

    public MySQLStorage()
    {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + Config.DB_URL);
        hikariConfig.setUsername(Config.DB_USERNAME);
        hikariConfig.setPassword(Config.DB_PASSWORD);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("allowPublicKeyRetrieval", Config.DB_ALLOW_PUBLIC_KEY_RETRIEVAL);
        hikariConfig.addDataSourceProperty("useSSL", Config.DB_USE_SSL);
        this.dataSource = new HikariDataSource(hikariConfig);

        this.setup();
    }

    private void setup()
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(Queries.CREATE_TABLE);
            statement.executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();
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
        Bukkit.getScheduler().runTaskAsynchronously(EnhancedHealth.getInstance(), () -> saveData(healthData));
    }

    @Override
    public HealthData loadData(UUID uuid)
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(Queries.GET);
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                double extraHealth = result.getDouble("extra_health");
                double health = result.getDouble("health");
                return new HealthData(uuid, extraHealth, health);
            } else
            {
                return new HealthData(uuid);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveData(HealthData healthData)
    {
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(Queries.INSERT);
            statement.setString(1, healthData.getUuid().toString());
            statement.setDouble(2, healthData.getExtraHealth());
            statement.setDouble(3, healthData.getHealth());
            statement.setDouble(4, healthData.getExtraHealth());
            statement.setDouble(5, healthData.getHealth());
            statement.executeUpdate();
        } catch (SQLException e)
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
            try (Connection connection = getConnection())
            {
                PreparedStatement statement = connection.prepareStatement(Queries.GET_UUIDS);
                ResultSet result = statement.executeQuery();
                while (result.next())
                {
                    data.add(loadData(UUID.fromString(result.getString("uuid"))));
                }
            } catch (SQLException e)
            {
                e.printStackTrace();
                callback.call(null);
                return;
            }
            Bukkit.getScheduler().runTask(EnhancedHealth.getInstance(), () -> callback.call(data));
        });
    }

    private Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }

    private static class Queries
    {
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS enhancedhealth(uuid CHAR(36) NOT NULL,extra_health DOUBLE,health DOUBLE,PRIMARY KEY (uuid))";
        public static final String INSERT = "INSERT INTO enhancedhealth (uuid,extra_health,health) VALUES(?,?,?) ON DUPLICATE KEY UPDATE extra_health=?,health=?";
        public static final String GET = "SELECT extra_health,health FROM enhancedhealth WHERE uuid=?";
        public static final String GET_UUIDS = "SELECT uuid FROM enhancedhealth";
    }
}
