package io.apollosoftware.naturalnavigation.world;

import io.apollosoftware.naturalnavigation.exception.WorldNotExistException;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

/**
 * Class created by xenojava on 8/30/2015.
 */
public class World {

    @Getter
    @Setter
    private String name, title;

    @Getter
    @Setter
    private double minHeight, maxHeight, scale;

    public World(String name) throws WorldNotExistException {
        this.name = name;
    }

    public org.bukkit.World getBukkitWorld() {
        return Bukkit.getWorld(name);
    }

    public boolean isKeepInventory() {
        return Boolean.valueOf(getBukkitWorld().getGameRuleValue("keepInventory"));
    }
}
