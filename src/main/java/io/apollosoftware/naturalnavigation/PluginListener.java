package io.apollosoftware.naturalnavigation;

import io.apollosoftware.lib.EventListener;
import io.apollosoftware.lib.lang.Message;
import io.apollosoftware.naturalnavigation.data.HomeData;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.enums.PlayerState;
import io.apollosoftware.naturalnavigation.exception.WorldNotExistException;
import io.apollosoftware.naturalnavigation.lib.Title;
import io.apollosoftware.naturalnavigation.menu.BedManageMenu;
import io.apollosoftware.naturalnavigation.menu.NavigationMenu;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Bed;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Class created by xenojava on 8/30/2015.
 */
public class PluginListener extends EventListener<NaturalNavigation> {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent event) throws SQLException {
        PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getPlayer());

        if (data.isDead()) {
            event.setCancelled(true);
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (!event.hasBlock()) return;

        if (event.getClickedBlock().getType() != Material.BED_BLOCK) return;

        Player player = event.getPlayer();

        if (player.isSneaking()) return;

        new BedManageMenu(event.getPlayer().getUniqueId(), (Bed) event.getClickedBlock().getState().getData(), event.getClickedBlock().getLocation()).
                open(player);
        event.setCancelled(true);
    }


    @EventHandler
    public void onBannerUpdate(BlockPlaceEvent event) throws SQLException {
        if (event.getBlock().getType() != Material.WALL_BANNER) return;

        Location bedLocation = event.getBlock().getLocation().clone().subtract(0, 2, 0);

        if (bedLocation.getBlock() != null && bedLocation.getBlock().getType().equals(Material.BED_BLOCK)) {

            HomeData home = plugin.getHomesStorage().getHome(bedLocation);

            if (home != null) {

                Location bannerLocation = event.getBlock().getLocation();

                if (bannerLocation.getBlock() != null && bannerLocation.getBlock().getState() instanceof Banner) {
                    ItemStack bannerItem = new ItemStack(Material.BANNER);
                    Banner banner = (Banner) bannerLocation.getBlock().getState();
                    BannerMeta meta = (BannerMeta) bannerItem.getItemMeta();
                    meta.setPatterns(banner.getPatterns());
                    bannerItem.setItemMeta(meta);
                    bannerItem.setDurability(banner.getBaseColor().getDyeData());
                }
            }
        }
    }

    @EventHandler
    public void onBedBreak(BlockBreakEvent event) throws SQLException, WorldNotExistException {
        if (event.getBlock().getType() != Material.BED_BLOCK) return;

        Location headOfBed = plugin.getHomesStorage().getHeadOfBed((Bed) event.getBlock().getState().getData(), event.getBlock().getLocation());
        HomeData home = plugin.getHomesStorage().getHome(headOfBed);

        if (home != null) {
            plugin.getHomesStorage().remove(home);
            Message.create("homeUnknownDelete").sendTo(event.getPlayer());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getPlayer());
        Player player = event.getPlayer();

        if (data.isDead() || data.getState(event.getPlayer()) == PlayerState.PRE) {
            String[] bar = plugin.getActionMessage(data.getState(event.getPlayer()));
            new Title(bar[0], bar[1]).send(event.getPlayer());
            event.getPlayer().setAllowFlight(true);
            for (Player p : Bukkit.getOnlinePlayers()) p.hidePlayer(event.getPlayer());
            player.setGameMode(GameMode.ADVENTURE);
            return;
        }


        for (PlayerData d : plugin.getPlayerStorage().getUsers().values())
            if (d.isDead() && Bukkit.getPlayer(d.getUUID()) != null)
                event.getPlayer().hidePlayer(Bukkit.getPlayer(d.getUUID()));

        ItemStack item = event.getPlayer().getInventory().getItem(4);
        if (event.getPlayer().getInventory().getItem(4) == null) return;
        if (!event.getPlayer().getInventory().getItem(4).hasItemMeta()) return;

        PlayerState prevState = plugin.getPreviousState(item.getItemMeta().getDisplayName());
        if (prevState == PlayerState.PRE || prevState == PlayerState.DEAD) {

            for (Player p : Bukkit.getOnlinePlayers()) p.showPlayer(player);
            player.setGameMode(GameMode.SURVIVAL);
            if (!plugin.getWorld(player).isKeepInventory())
                player.getInventory().clear();
            new Title("").resetTitle(player);
            player.setAllowFlight(false);

        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) throws SQLException {
        if (!(event.getEntity() instanceof Player)) return;

        Player target = (Player) event.getEntity();

        PlayerData data = plugin.getPlayerStorage().createIfNotExists(target);
        if (data.isDead() || data.getState(target) == PlayerState.PRE) event.setCancelled(true);
    }

    @EventHandler
    public void onTaget(EntityTargetEvent event) throws SQLException {
        if (!(event.getTarget() instanceof Player)) return;

        Player target = (Player) event.getTarget();

        PlayerData data = plugin.getPlayerStorage().createIfNotExists(target);
        if (data.isDead() || data.getState(target) == PlayerState.PRE) event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) throws SQLException {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        PlayerData data = plugin.getPlayerStorage().createIfNotExists(player);
        if (data.isDead() || data.getState(player) == PlayerState.PRE) event.setCancelled(true);
    }

    @EventHandler
    public void onClickItem(InventoryClickEvent event) throws SQLException {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        if (!(event.getClickedInventory() instanceof PlayerInventory)) return;

        PlayerData data = plugin.getPlayerStorage().createIfNotExists(player);
        if (data.isDead() || data.getState(player) == PlayerState.PRE) event.setCancelled(true);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) throws SQLException {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();

        PlayerData data = plugin.getPlayerStorage().createIfNotExists(player);
        if (data.isDead() || data.getState(player) == PlayerState.PRE) event.setCancelled(true);
    }

    @EventHandler
    public void onPickupEvent(PlayerPickupItemEvent event) throws SQLException {
        PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getPlayer());
        if (data.isDead() || data.getState(event.getPlayer()) == PlayerState.PRE) event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) throws SQLException {
        PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getPlayer());
        if (data.isDead() || data.getState(event.getPlayer()) == PlayerState.PRE) event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) throws SQLException {
        PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getEntity());
        data.setDeathLocation(event.getEntity().getLocation());

        if (plugin.getConfiguration().isDeadStateEnabled()) {
            data.isDead(event.getEntity(), true);
            plugin.getPlayerStorage().update(data);
            event.getEntity().spigot().respawn();
        }
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) throws SQLException {
        final PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getPlayer());
        event.setRespawnLocation(data.getDeathLocation());
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                event.getPlayer().setGameMode(GameMode.ADVENTURE);
                event.getPlayer().setAllowFlight(true);
                if (plugin.getConfiguration().getAutoSetTarget().getBoolean("on_death"))
                    event.getPlayer().setCompassTarget(data.getDeathLocation());
                for (Player p : Bukkit.getOnlinePlayers()) p.hidePlayer(event.getPlayer());

                if (!plugin.getWorld(event.getPlayer()).isKeepInventory())
                    plugin.setInventoryLayout(data, event.getPlayer());

                String[] bar = plugin.getActionMessage(data.getState(event.getPlayer()));
                new Title(bar[0], bar[1]).send(event.getPlayer());
            }
        });
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        for (HomeData home : plugin.getHomesStorage().getAllHomes(event.getWorld().getName())) home.updateBanner();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNavTool(PlayerInteractEvent event) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE && plugin.getConfiguration().isWorldEditToolCreative())
            return;

        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) return;
        if (event.getPlayer().getItemInHand().getType() != plugin.getConfiguration().getNavTool().getType()) return;

        PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getPlayer());
        new NavigationMenu(data.getState(event.getPlayer())).open(event.getPlayer());
        event.setCancelled(true);
    }


}
