package io.apollosoftware.naturalnavigation.biome;

import lombok.Getter;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Class created by xenojava on 9/5/2015.
 */
public class BiomeGroup extends ArrayList<Biome> {

    @Getter
    private ItemStack icon;

    @Getter
    private String title;


    public BiomeGroup(ItemStack icon, String title, List<String> biomeStringList) {
        super(new ArrayList<Biome>());
        this.icon = icon;
        this.title = title;
        for (String biome : biomeStringList) add(Biome.valueOf(biome));
    }
}
