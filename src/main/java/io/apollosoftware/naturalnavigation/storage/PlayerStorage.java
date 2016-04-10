package io.apollosoftware.naturalnavigation.storage;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import io.apollosoftware.naturalnavigation.configuration.mysql.LocalDatabaseConfiguration;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.configuration.mysql.LocalDatabaseConfiguration;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class created by xenojava on 8/18/2015.
 */
public class PlayerStorage extends BaseDaoImpl<PlayerData, byte[]> {


    @Getter
    private Map<UUID, PlayerData> users = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public PlayerStorage(NaturalNavigation plugin, LocalDatabaseConfiguration databaseConf, ConnectionSource connection) throws SQLException {
        super(connection, (DatabaseTableConfig<PlayerData>)
                databaseConf.getTable("playersTable"));

        if (!this.isTableExists())
            TableUtils.createTable(connection, tableConfig);


        CloseableIterator<PlayerData> itr = iterator();

        while (itr.hasNext()) {
            PlayerData user = itr.next();
            users.put(user.getUUID(), user);
        }

        itr.close();

        plugin.getLogger().info("Loaded " + users.size() + " users into memory");
    }


    public PlayerData createIfNotExists(Player player) throws SQLException {
        PlayerData user = users.get(player.getUniqueId());

        if (user != null) return user;

        user = new PlayerData(player.getUniqueId(), player.getName());
        create(user);
        users.put(player.getUniqueId(), user);

        return user;
    }


}
