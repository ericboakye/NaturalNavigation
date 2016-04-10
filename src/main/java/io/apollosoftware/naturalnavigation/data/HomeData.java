package io.apollosoftware.naturalnavigation.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.apollosoftware.naturalnavigation.storage.HomeStorage;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

@DatabaseTable(tableName = "homes", daoClass = HomeStorage.class)
public class HomeData {

    @Getter
    @DatabaseField(generatedId = true)
    private int id;

    @Getter
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private PlayerData owner;

    @Getter
    @DatabaseField(columnDefinition = "TINYINT(2)", canBeNull = false)
    private byte baseDyeColor;

    @Setter
    @Getter
    @DatabaseField(columnDefinition = "VARCHAR(17)", canBeNull = true)
    private String name;

    @Setter
    @Getter
    @DatabaseField(columnDefinition = "VARCHAR(17)", canBeNull = false)
    private String world;

    @Setter
    @DatabaseField(columnDefinition = "BIGINT", canBeNull = false)
    private int x, y, z;


    @Setter
    private ItemStack banner;

    HomeData() {
        // for ormlite
    }

    public HomeData(PlayerData owner, String name, ItemStack banner, Location location) {
        this.owner = owner;
        this.banner = banner;
        this.baseDyeColor = (byte) banner.getDurability();
        this.name = name;
        this.world = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }


    public ItemStack getBanner() {
        if (banner == null) return new ItemStack(Material.BANNER, 1, baseDyeColor);
        return banner;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public void updateBanner() {
        Location bannerLocation = getLocation().clone().add(0, 2, 0);
        ItemStack bannerItem = new ItemStack(Material.BANNER);

        if (bannerLocation.getBlock() != null && bannerLocation.getBlock().getState() instanceof Banner) {
            Banner banner = (Banner) bannerLocation.getBlock().getState();
            BannerMeta meta = (BannerMeta) bannerItem.getItemMeta();
            meta.setPatterns(banner.getPatterns());
            bannerItem.setItemMeta(meta);
            bannerItem.setDurability(banner.getBaseColor().getDyeData());
        } else bannerItem.setDurability(baseDyeColor);
        setBanner(bannerItem);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof HomeData) && ((HomeData) obj).getId() == id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
