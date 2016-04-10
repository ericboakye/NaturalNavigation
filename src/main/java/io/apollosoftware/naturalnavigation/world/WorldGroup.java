package io.apollosoftware.naturalnavigation.world;

import io.apollosoftware.naturalnavigation.exception.WorldNotExistException;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class created by xenojava on 8/30/2015.
 */
public class WorldGroup extends HashMap<String, World> {

    @Getter
    private String name;

    @Getter
    private String title;

    @Getter
    private ItemStack icon;

    public WorldGroup(String name, String title) {
        super(new HashMap<String, World>());
        this.name = name;
        this.title = title;
    }


    /**
     * @param icon      Icon of group
     * @param worlds    Worlds part of group
     * @param allWorlds All worlds in config
     * @throws WorldNotExistException
     */
    public void load(ItemStack icon, List<String> worlds, List<Map<String, Object>> allWorlds) throws WorldNotExistException {
        this.icon = icon;
        for (Map<String, Object> worldMap : allWorlds) {
            String worldName = (String) worldMap.get("name");
            if (!worlds.contains(worldName)) continue;
            World world = new World(worldName);
            world.setTitle((String) worldMap.get("title"));
            if (worldMap.get("scale") != null)
                world.setScale((double) worldMap.get("scale"));
            world.setMinHeight((double) worldMap.get("min_height"));
            world.setMaxHeight((double) worldMap.get("max_height"));
            put(worldName, world);
        }
    }


    @Override
    public boolean equals(Object o) {
        return (o instanceof WorldGroup) && ((WorldGroup) o).getName().equalsIgnoreCase(name);
    }
}
