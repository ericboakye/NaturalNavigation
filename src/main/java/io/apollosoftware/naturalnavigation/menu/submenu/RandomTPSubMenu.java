package io.apollosoftware.naturalnavigation.menu.submenu;

import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.data.PlayerData;

import io.apollosoftware.lib.gui.Menu;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Random;

/**
 * Class created by xenojava on 8/30/2015.
 */
public class RandomTPSubMenu extends Menu<NaturalNavigation> {


    public RandomTPSubMenu(String title) {
        super(title, Menu.round(NaturalNavigation.getPlugin().getBiomesGroups().size()));
    }


    @Override
    public void open(Player player) throws Exception {
        int min = plugin.getConfiguration().getRandomtp().getInt("min_distance");
        int max = plugin.getConfiguration().getRandomtp().getInt("max_distance");

        int x = randomDoughnut(min, max);
        int y = 63;
        int z = randomDoughnut(min, max);

        Location loc = new Location(player.getWorld(), x, y, z);
        loc.setY(loc.getWorld().getHighestBlockYAt(loc));
        while (isDangerous(loc)) {
            x = randomDoughnut(min, max);
            z = randomDoughnut(min, max);

            loc = new Location(player.getWorld(), x, y, z);
            loc.setY(loc.getWorld().getHighestBlockYAt(loc));
        }

        loc.getChunk().load();

        player.teleport(loc);
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        PlayerData data = plugin.getPlayerStorage().createIfNotExists(player);

        if (data.isDead()) {
            data.isDead(player, false);
            plugin.getPlayerStorage().update(data);
        }
    }

    public static int randomDoughnut(int min, int max) {
        Random rand = new Random();
        int n = rand.nextInt(max) + min;

        if (rand.nextBoolean()) {
            return n * -1;
        } else {
            return n;
        }
    }

    private boolean isDangerous(Location location) {
        Block block = location.getBlock();
        return block != null && plugin.getDangerBlocks().containsKey(block.getType()) &&
                plugin.getDangerBlocks().get(block.getType()) == block.getData() && location.add(0.0D, 2.0D, 0.0D).getBlock().getType() == Material.AIR;
    }
}
