package io.apollosoftware.naturalnavigation.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.apollosoftware.naturalnavigation.menu.button.WarpIcon;
import io.apollosoftware.naturalnavigation.storage.WarpStorage;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@DatabaseTable(tableName = "warps", daoClass = WarpStorage.class)
public class WarpData {

    @Getter
    @DatabaseField(generatedId = true)
    private int id;

    @Getter
    @DatabaseField(columnDefinition = "VARCHAR(32)", canBeNull = false)
    private String material;

    @Getter
    @DatabaseField(columnDefinition = "INT(32)", canBeNull = false)
    private int data;

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
    @DatabaseField(columnDefinition = "FLOAT", canBeNull = false)
    private float pitch, yaw;

    WarpData() {
        // for ormlite
    }

    public WarpData(String name, WarpIcon icon, Location location) {
        this.name = name;
        this.material = icon.getItemStack().getType().name();
        this.data = icon.getItemStack().getDurability();
        this.world = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }


    public ItemStack getWarpIcon() {
        return new ItemStack(Material.matchMaterial(material), 1, (byte) data);
    }


    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof WarpData) && ((WarpData) obj).getId() == id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
