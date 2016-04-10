package io.apollosoftware.naturalnavigation.storage;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;
import io.apollosoftware.naturalnavigation.data.WarpData;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.configuration.mysql.LocalDatabaseConfiguration;
import io.apollosoftware.naturalnavigation.data.WarpData;
import io.apollosoftware.naturalnavigation.menu.button.WarpIcon;
import io.apollosoftware.naturalnavigation.world.WorldGroup;
import lombok.Getter;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class created by xenojava on 8/12/2015.
 */
public class WarpStorage extends BaseDaoImpl<WarpData, Integer> {

    @Getter
    private Map<Integer, WarpData> warps = new ConcurrentHashMap<>();

    @Getter
    private NaturalNavigation plugin;

    public WarpData createWarp(String name, WarpIcon icon, Location location) throws SQLException {

        WarpData newWarp = new WarpData(name, icon, location);
        create(newWarp);
        warps.put(newWarp.getId(), newWarp);
        return newWarp;
    }

    public List<WarpData> getWarps(WorldGroup group) {
        List<WarpData> warpDatas = new ArrayList<>();

        for (WarpData warp : warps.values())
            if (plugin.getWorldGroup(warp.getWorld()).equals(group)) warpDatas.add(warp);

        return warpDatas;
    }

    public void remove(WarpData warpData) throws SQLException {
        warps.remove(warpData.getId());
        delete(warpData);
    }

    @SuppressWarnings("unchecked")
    public WarpStorage(NaturalNavigation plugin, LocalDatabaseConfiguration databaseConf, ConnectionSource connection) throws SQLException {
        super(connection, (DatabaseTableConfig<WarpData>)
                databaseConf.getTable("warpsTable"));

        this.plugin = plugin;

        if (!this.isTableExists()) TableUtils.createTable(connection, tableConfig);

        CloseableIterator<WarpData> itr = iterator();

        while (itr.hasNext()) {
            WarpData warpData = itr.next();
            warps.put(warpData.getId(), warpData);
        }

        itr.close();
        plugin.getLogger().info("Loaded " + warps.size() + " warps into memory");
    }

}
