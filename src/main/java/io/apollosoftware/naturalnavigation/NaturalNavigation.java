package io.apollosoftware.naturalnavigation;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import io.apollosoftware.lib.Manifest;
import io.apollosoftware.lib.ServerPlugin;
import io.apollosoftware.lib.lang.Message;
import io.apollosoftware.naturalnavigation.biome.BiomeGroup;
import io.apollosoftware.naturalnavigation.command.PluginCommandWrapper;
import io.apollosoftware.naturalnavigation.configuration.DefaultConfiguration;
import io.apollosoftware.naturalnavigation.configuration.LanguageConfiguration;
import io.apollosoftware.naturalnavigation.configuration.mysql.LocalDatabaseConfiguration;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.data.mysql.MySQLDatabase;
import io.apollosoftware.naturalnavigation.enums.PlayerState;
import io.apollosoftware.naturalnavigation.exception.WorldNotExistException;
import io.apollosoftware.naturalnavigation.storage.HomeStorage;
import io.apollosoftware.naturalnavigation.storage.PlayerStorage;
import io.apollosoftware.naturalnavigation.storage.WarpStorage;
import io.apollosoftware.naturalnavigation.world.World;
import io.apollosoftware.naturalnavigation.world.WorldGroup;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

@Manifest(name = "NaturalNavigation", version = "v1.0 BETA")
public class NaturalNavigation extends ServerPlugin {


    @Getter
    private List<WorldGroup> worldGroups = new ArrayList<>();

    @Getter
    private List<BiomeGroup> biomesGroups = new ArrayList<>();

    @Getter
    private JdbcPooledConnectionSource jdbcConnectionSource;

    private LocalDatabaseConfiguration localDB;

    @Getter
    private PlayerStorage playerStorage;

    @Getter
    private HomeStorage homesStorage;

    @Getter
    private WarpStorage warpStorage;

    @Getter
    private DefaultConfiguration configuration;

    @Getter
    private LanguageConfiguration languageConfiguration;

    @Getter
    private PluginCommandWrapper commandWrapper;

    @Getter
    private static NaturalNavigation plugin;

    @Getter
    private Map<Material, Byte> dangerBlocks = new HashMap<>();


    public void init(Manifest m) throws Exception {
        plugin = this;

        setupConfigs();
        setupWorldGroups();
        setupRandomTeleportConfig();

        try {
            long startTime = System.currentTimeMillis();
            getLogger().info("Connecting to MySQL Server [...]");
            this.localDB = new LocalDatabaseConfiguration(configuration.conf.getConfigurationSection("databaseConnection"));
            this.jdbcConnectionSource = setupConnection();


            getLogger().info("Attempting to load local storage drivers and database [...]");

            setupStorages();

            getLogger().info("Successfully connected to MySQL database! [" +
                    ((System.currentTimeMillis() - startTime)) + "ms]");

            setupPermissions();
            setupListeners();


        } catch (SQLException e) {
            getLogger().info("An error occurred attempting to make a database connection, please see stack trace below");
            getServer().getPluginManager().disablePlugin(this);
            e.printStackTrace();
            return;
        }

        commandWrapper = PluginCommandWrapper.register();
    }

    public void reload() {
        worldGroups.clear();
        dangerBlocks.clear();
        biomesGroups.clear();

        setupWorldGroups();
        setupRandomTeleportConfig();
        setupPermissions();
        setupListeners();
    }

    private JdbcPooledConnectionSource setupConnection() throws SQLException {
        getLogger().info("Establishing pooled connection to server...");
        JdbcPooledConnectionSource connection = new JdbcPooledConnectionSource(localDB.getJDBCUrl());


        if (!localDB.getUser().isEmpty())
            connection.setUsername(localDB.getUser());

        if (!localDB.getPassword().isEmpty())
            connection.setPassword(localDB.getPassword());


        connection.setMaxConnectionsFree(localDB.getMaxConnections());

        connection.setTestBeforeGet(false);
        connection.setMaxConnectionAgeMillis(900000);

        connection.setCheckConnectionsEveryMillis(0);
        connection.setDatabaseType(new MySQLDatabase());
        connection.initialize();

        return connection;

    }

    public void setupListeners() {
        new PluginListener().register();
    }

    public void setupPermissions() {
        for (int i = 0; i < configuration.getMaximumHomes(); i++)
            getServer().getPluginManager().addPermission(new Permission("nav.homes.limit." + i));
    }


    public void setupStorages() throws SQLException, WorldNotExistException {
        this.playerStorage = new PlayerStorage(this, localDB, jdbcConnectionSource);
        this.homesStorage = new HomeStorage(this, localDB, jdbcConnectionSource);
        this.warpStorage = new WarpStorage(this, localDB, jdbcConnectionSource);
    }

    public World getWorld(Player player) {
        for (WorldGroup group : worldGroups)
            for (World world : group.values())
                if (world.getName().equalsIgnoreCase(player.getWorld().getName())) return world;
        return null;
    }


    public WorldGroup getWorldGroup(String worldname) {
        for (WorldGroup group : worldGroups)
            for (World world : group.values())
                if (world.getName().equalsIgnoreCase(worldname)) return group;
        return null;
    }


    public void setupConfigs() {

        saveDefaultConfig();
        configuration = new DefaultConfiguration();

        if (!configuration.getFile().exists()) {
            getLogger().info("Step 3 Complete! Please configure NaturalNavigation to get started.");
            getServer().getPluginManager().disablePlugin(this);
        }

        configuration.load();
        languageConfiguration = new LanguageConfiguration();
        languageConfiguration.load();

    }

    public void setupWorldGroups() {
        for (String worldGroupName : configuration.getWorldGroups().getKeys(false)) {
            List<String> worlds = configuration.getWorldGroups().getStringList(worldGroupName + ".worlds");
            String title = configuration.getWorldGroups().getString(worldGroupName + ".title");
            String icon = configuration.getWorldGroups().getString(worldGroupName + ".icon");
            // World group created here
            WorldGroup group = new WorldGroup(worldGroupName, title);
            // Adds the selected worlds to group if world does not load loop continues
            try {
                group.load(parse(icon), worlds, configuration.getWorlds());
            } catch (WorldNotExistException e) {
                e.printStackTrace();
                continue;
            }
            worldGroups.add(group);
        }
    }

    public void setInventoryLayout(PlayerData data, Player player) {
        for (int i = 0; i <= 8; i++) {

            ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(" ");
            item.setItemMeta(meta);

            if (i == 4) {
                item = new ItemStack(Material.COMPASS);
                ItemMeta compassMeta = item.getItemMeta();
                compassMeta.setDisplayName(getCompassName(data.getState(player)));
                item.setItemMeta(compassMeta);
            }

            player.getInventory().setItem(i, item);
        }
    }

    @SuppressWarnings("unchecked")
    public void setupRandomTeleportConfig() {
        for (String s : plugin.getConfiguration().getRandomtp().getStringList("danger_blocks")) {
            ItemStack item = plugin.parse(s);
            this.dangerBlocks.put(item.getType(), (byte) item.getDurability());
        }

        for (Map<String, Object> map : configuration.getBiomesGroups()) {
            List<String> biomes = (List<String>) map.get("biomes");
            String icon = (String) map.get("icon");
            String title = (String) map.get("title");

            // Biome Group.
            BiomeGroup group = new BiomeGroup(plugin.parse(icon), title, biomes);
            biomesGroups.add(group);
        }
    }


    public PlayerState getPreviousState(String message) {
        if (message.equals(Message.getString("preGameStateCompassName"))) return PlayerState.PRE;
        else if (message.equals(Message.getString("deadStateCompassName"))) return PlayerState.DEAD;
        return null;
    }

    public String getCompassName(PlayerState state) {
        switch (state) {
            case PRE:
                return Message.getString("preGameStateCompassName");
            case DEAD:
                return Message.getString("deadStateCompassName");
            case PLAYING:
                return null;
        }
        return null;
    }

    public String[] getActionMessage(PlayerState state) {
        switch (state) {
            case PRE:
                return new String[]{Message.getString("actionBarStartTitle"), Message.getString("actionBarStartSubtitle")};
            case DEAD:
                return new String[]{Message.getString("actionBarDeadTitle"), Message.getString("actionBarDeadSubtitle")};
            case PLAYING:
                return null;
        }
        return null;
    }

    public ItemStack parse(String item) {
        if (item.contains(":")) {
            String[] withData = item.split(":");
            Material material = Material.matchMaterial(withData[0]);
            byte data = Byte.parseByte(withData[1]);
            return new ItemStack(material, 1, (short) data);
        }

        Material material = Material.matchMaterial(item);
        return new ItemStack(material);
    }

}
