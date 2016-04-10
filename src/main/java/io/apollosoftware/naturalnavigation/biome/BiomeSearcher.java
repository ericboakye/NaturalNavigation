package io.apollosoftware.naturalnavigation.biome;

import io.apollosoftware.naturalnavigation.NaturalNavigation;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BiomeSearcher {

    private BiomeGroup group;
    private NaturalNavigation plugin;

    private List<Location> locations = new ArrayList<>();

    private long timeout;
    private boolean isReady = false;

    private BukkitTask locationsFinder;

    public BiomeSearcher(final NaturalNavigation plugin, BiomeGroup group, final World world) {
        this.plugin = plugin;
        this.group = group;
        this.timeout = System.currentTimeMillis() + plugin.getConfiguration().getSearchTimeoutInMillis();


        // Find all locations
        locationsFinder = Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                int min = plugin.getConfiguration().getRandomtp().getInt("min_distance");
                int max = plugin.getConfiguration().getRandomtp().getInt("max_distance");

                for (int x = min / 16; x < max / 16; x++) {
                    for (int z = min / 16; z < max / 16; z++) {
                        Chunk chunk = world.getChunkAt(x, z);

                        if (chunk == null) continue;

                        if (!chunk.isLoaded()) continue;

                        int chunkX = chunk.getX() << 4;
                        int chunkZ = chunk.getZ() << 4;

                        World world = chunk.getWorld();

                        for (int xx = chunkX; xx < chunkX + 16; xx++) {
                            for (int zz = chunkZ; zz < chunkZ + 16; zz++) {
                                Location location = new Location(world, xx, 63, zz);
                                locations.add(location);
                            }
                        }

                    }
                }

                isReady = true;
                System.out.print(locations.size() + " blocks!");
            }
        });

        // Randomize the list
        Collections.shuffle(locations);

    }

    public Location search() {
        // Halt until is ready within timeout
        while (!isReady)
            if (System.currentTimeMillis() > timeout) {
                locationsFinder.cancel();
                return null;
            }

        for (Location location : locations)
            if (!isDangerous(location) && (!group.isEmpty() && group.contains(location.getBlock().getBiome())))
                return location;

        return null;
    }

    private boolean isDangerous(Location location) {
        Block block = location.getBlock();
        return block != null && plugin.getDangerBlocks().containsKey(block.getType()) &&
                plugin.getDangerBlocks().get(block.getType()) == block.getData();
    }

}