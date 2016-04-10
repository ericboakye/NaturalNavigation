package io.apollosoftware.naturalnavigation.configuration;

import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.lib.configuration.Configuration;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class DefaultConfiguration extends Configuration<NaturalNavigation> {


    public DefaultConfiguration() {
        super("config.yml");
    }

    @Getter
    private List<Map<String, Object>> worlds;


    @Getter
    private List<Map<String, Object>> biomesGroups;

    @Getter
    private ConfigurationSection randomtp;

    @Getter
    private ConfigurationSection worldGroups;

    private boolean worldEditToolCreative;

    @Getter
    private boolean preGameStateEnabled, deadStateEnabled;

    @Getter
    private ItemStack navTool;

    @Getter
    private ConfigurationSection autoSetTarget;

    @Getter
    private int maximumHomes;

    @Getter
    private int searchTimeoutInMillis;


    @SuppressWarnings("unchecked")
    public void afterLoad() {
        this.worlds = (List<Map<String, Object>>) conf.get("worlds");
        this.randomtp = conf.getConfigurationSection("randomtp");
        this.preGameStateEnabled = conf.getBoolean("preGameStateEnabled");
        this.deadStateEnabled = conf.getBoolean("deadStateEnabled");
        this.searchTimeoutInMillis = randomtp.getInt("timeout");
        this.biomesGroups = (List<Map<String, Object>>) randomtp.get("biome_groups");
        this.autoSetTarget = conf.getConfigurationSection("auto_set_target");
        this.worldGroups = conf.getConfigurationSection("world_groups");
        this.maximumHomes = conf.getInt("maximumHomes");
        this.worldEditToolCreative = conf.getBoolean("worldedit_tool_creative");
        this.navTool = plugin.parse(conf.getString("nav_tool"));
    }


    public void onSave() {
        // I got nothing m8
    }

    public boolean isWorldEditToolCreative() {
        return worldEditToolCreative && Bukkit.getPluginManager().getPlugin("WorldEdit") != null;
    }
}
