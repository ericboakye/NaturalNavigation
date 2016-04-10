package io.apollosoftware.naturalnavigation.configuration.mysql;

import com.j256.ormlite.table.DatabaseTableConfig;
import io.apollosoftware.naturalnavigation.data.HomeData;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.data.WarpData;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class LocalDatabaseConfiguration extends DatabaseConfiguration {

    private HashMap<String, DatabaseTableConfig<?>> tables = new HashMap<String, DatabaseTableConfig<?>>();

    public LocalDatabaseConfiguration(ConfigurationSection conf) {
        super(conf);


        /**
         * This is where we initialize the tables
         * configuration.
         */

        DatabaseTableConfig<PlayerData> playersTable = new DatabaseTableConfig<>(PlayerData.class, "players", null);
        addTable("playersTable", playersTable);

        DatabaseTableConfig<HomeData> homesTable = new DatabaseTableConfig<>(HomeData.class, "homes", null);
        addTable("homesTable", homesTable);

        DatabaseTableConfig<WarpData> warpsTable = new DatabaseTableConfig<>(WarpData.class, "warps", null);
        addTable("warpsTable", warpsTable);
    }

    public DatabaseTableConfig<?> getTable(String table) {
        return tables.get(table);
    }

    public void addTable(String key, DatabaseTableConfig<?> config) {
        tables.put(key, config);
    }
}
