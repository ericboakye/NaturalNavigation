package io.apollosoftware.naturalnavigation.storage;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.configuration.mysql.LocalDatabaseConfiguration;
import io.apollosoftware.naturalnavigation.data.HomeData;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.exception.WorldNotExistException;
import io.apollosoftware.naturalnavigation.world.WorldGroup;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Bed;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class created by xenojava on 8/12/2015.
 */
public class HomeStorage extends BaseDaoImpl<HomeData, Integer> {

    @Getter
    private Map<UUID, List<HomeData>> homes = new ConcurrentHashMap<>();

    private NaturalNavigation plugin;

    public HomeData createHome(PlayerData owner, String name, Bed bed, Location location) throws SQLException {
        Location bannerLocation = getHeadOfBed(bed, location).clone().add(0, 2, 0);
        ItemStack bannerItem = new ItemStack(Material.BANNER);

        if (bannerLocation.getBlock() != null && bannerLocation.getBlock().getState() instanceof Banner) {
            Banner banner = (Banner) bannerLocation.getBlock().getState();
            BannerMeta meta = (BannerMeta) bannerItem.getItemMeta();
            meta.setPatterns(banner.getPatterns());
            bannerItem.setItemMeta(meta);
            bannerItem.setDurability(banner.getBaseColor().getDyeData());
        } else {
            bannerItem.setDurability(DyeColor.values()[new Random().nextInt(16)].getDyeData());
        }


        HomeData newhome = new HomeData(owner, name, bannerItem, getHeadOfBed(bed, location));
        create(newhome);
        getHomes(owner).add(newhome);
        return newhome;
    }

    public Location getHeadOfBed(Bed bed, Location loc) {
        if (bed.isHeadOfBed()) return loc;
        else return loc.getBlock().getRelative(bed.getFacing()).getLocation();
    }

    public HomeData getHome(Location location) {
        for (List<HomeData> homeList : homes.values())
            for (HomeData home : homeList)
                if (home.getLocation().getWorld().getName().equals(location.getWorld().getName()) && home.getLocation().distance(location) <= 2)
                    return home;
        return null;
    }


    public List<HomeData> getAllHomes(String worldName) {
        List<HomeData> homes = new ArrayList<>();
        for (List<HomeData> homeList : this.homes.values())
            for (HomeData home : homeList)
                if (home.getWorld().equalsIgnoreCase(worldName))
                    homes.add(home);
        return homes;
    }

    public boolean exists(Location location) {
        for (List<HomeData> homeList : homes.values())
            for (HomeData home : homeList) {
                if (home.getLocation().getWorld().getName().equals(location.getWorld().getName()) && home.getLocation().distance(location) <= 2)
                    return true;
            }
        return false;
    }


    public List<HomeData> getHomes(WorldGroup group, PlayerData owner) {
        List<HomeData> homesCollection;

        if (homes.get(owner.getUUID()) == null) {
            homesCollection = new ArrayList<>();
            homes.put(owner.getUUID(), homesCollection);
            return homesCollection;
        }
        homesCollection = new ArrayList<>();

        for (HomeData homeData : homes.get(owner.getUUID()))
            if (plugin.getWorldGroup(homeData.getWorld()).equals(group)) homesCollection.add(homeData);

        return homesCollection;
    }


    public List<HomeData> getHomes(PlayerData owner) {
        List<HomeData> homesCollection = homes.get(owner.getUUID());
        if (homesCollection == null) {
            homesCollection = new ArrayList<>();
            homes.put(owner.getUUID(), homesCollection);
        }
        return homesCollection;
    }


    public void remove(HomeData home) throws SQLException {
        getHomes(home.getOwner()).remove(home);
        delete(home);
    }

    @SuppressWarnings("unchecked")
    public HomeStorage(NaturalNavigation plugin, LocalDatabaseConfiguration databaseConf, ConnectionSource connection) throws SQLException, WorldNotExistException {
        super(connection, (DatabaseTableConfig<HomeData>)
                databaseConf.getTable("homesTable"));

        this.plugin = plugin;

        if (!this.isTableExists()) TableUtils.createTable(connection, tableConfig);


        CloseableIterator<HomeData> itr = iterator();

        while (itr.hasNext()) {
            HomeData home = itr.next();

            List<HomeData> homesCollection = homes.get(home.getOwner().getUUID());

            if (homesCollection == null) homesCollection = new ArrayList<>();
            homesCollection.add(home);

            homes.put(home.getOwner().getUUID(), homesCollection);

            if(Bukkit.getWorld(home.getWorld()) != null)
                home.updateBanner();
        }

        itr.close();
        plugin.getLogger().info("Loaded " + homes.size() + " homes into memory");
    }

}
