package io.apollosoftware.naturalnavigation.menu;

import io.apollosoftware.lib.ColorConverter;
import io.apollosoftware.lib.gui.GUIButton;
import io.apollosoftware.lib.gui.GUIButtonClickEvent;
import io.apollosoftware.lib.gui.Menu;
import io.apollosoftware.lib.lang.Message;
import io.apollosoftware.naturalnavigation.menu.util.NamingSession;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.data.HomeData;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.menu.util.NamingSession;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Class created by xenojava on 9/1/2015.
 */
public class BedManageMenu extends Menu<NaturalNavigation> {


    public BedManageMenu(UUID uuid, final Bed bed, final Location location)  {
        super("Home Management", 9, uuid);

        setDestroyOnExit(true);

        if (!plugin.getHomesStorage().exists(plugin.getHomesStorage().getHeadOfBed(bed, location))) {
            GUIButton setHome = new GUIButton(Material.BED) {
                @Override
                public void action(GUIButtonClickEvent event) throws SQLException {
                    final Player player = event.getWhoClicked();

                    final PlayerData data = plugin.getPlayerStorage().createIfNotExists(player);

                    new NamingSession(player, new NamingSession.NamingEventHandler() {
                        @Override
                        public void onName(NamingSession.NamingEvent event) throws SQLException {

                            if (event.getName().length() > 17) {
                                Message.create("homeNameMax").sendTo(player);
                                return;
                            }

                            if (!player.isOp() && !player.hasPermission("nav.homes.limit." + plugin.getHomesStorage().getHomes(data) + 1)) {
                                Message.create("homeNoPermission").sendTo(player);
                                return;
                            }

                            HomeData newHome = plugin.getHomesStorage().createHome(data, event.getName(), bed, location);
                            ChatColor color = ColorConverter.dyeToChat(DyeColor.getByDyeData(newHome.getBaseDyeColor()));

                            Message.create("homeCreate").param(color, newHome.getName()).sendTo(player);


                            if (plugin.getConfiguration().getAutoSetTarget().getBoolean("on_sethome"))
                                player.setCompassTarget(location);
                        }
                    });

                    event.getWhoClicked().closeInventory();
                }
            };

            setHome.setDisplayName(ChatColor.DARK_GREEN + "Set home");
            set(size(), setHome);
        }


        GUIButton compassTarget = new GUIButton(Material.COMPASS) {
            @Override
            public void action(GUIButtonClickEvent event) {
                final Player player = event.getWhoClicked();
                Message.create("compassTargetUpdate").sendTo(player);
                player.setCompassTarget(plugin.getHomesStorage().getHeadOfBed(bed, location));
                player.closeInventory();
            }
        };
        compassTarget.setDisplayName(ChatColor.DARK_GRAY + "Set compass target");
        set(size(), compassTarget);

        GUIButton sleep = new GUIButton(Material.WATCH) {
            @Override
            public void action(GUIButtonClickEvent event) {
                final Player player = event.getWhoClicked();
                player.closeInventory();
                Message.create("sleepNote").sendTo(player);
            }
        };

        sleep.setDisplayName(ChatColor.BLUE + "Sleep");
        set(size(), sleep);

    }

    @Override
    public void open(Player player) {
        player.openInventory(inventory);
    }
}
