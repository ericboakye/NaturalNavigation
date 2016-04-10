package io.apollosoftware.naturalnavigation.menu.submenu;

import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.data.HomeData;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.exception.WorldNotExistException;
import io.apollosoftware.naturalnavigation.menu.NavigationMenu;
import io.apollosoftware.naturalnavigation.menu.button.ActionButton;
import io.apollosoftware.naturalnavigation.menu.util.NamingSession;

import io.apollosoftware.lib.ColorConverter;
import io.apollosoftware.lib.gui.GUIButton;
import io.apollosoftware.lib.gui.GUIButtonClickEvent;
import io.apollosoftware.lib.gui.Menu;
import io.apollosoftware.lib.lang.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Class created by xenojava on 8/30/2015.
 */
public class HomeSubmenu extends Menu<NaturalNavigation> {


    private static List<HomeData> getHomes(NaturalNavigation plugin, PlayerData data) {
        final Player player = Bukkit.getPlayer(data.getUUID());
        return plugin.getHomesStorage().getHomes(plugin.getWorldGroup(player.getWorld().getName()), data);
    }

    public HomeSubmenu(String title, final PlayerData data) {
        super(title, Menu.round(getHomes(NaturalNavigation.getPlugin(), data).size() + 1), data.getUUID());
        setDestroyOnExit(true);

        final Player player = Bukkit.getPlayer(data.getUUID());

        if (getHomes(NaturalNavigation.getPlugin(), data).size() == 0) {
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    player.closeInventory();
                }
            });
            Message.create("noHomes").sendTo(player);
            return;
        }

        for (final HomeData home : getHomes(NaturalNavigation.getPlugin(), data)) {

            if (plugin.getWorldGroup(home.getWorld()) != plugin.getWorldGroup(player.getWorld().getName()))
                continue;

            final ChatColor color = ColorConverter.dyeToChat(DyeColor.getByDyeData(home.getBaseDyeColor()));

            ActionButton homeButton = new ActionButton(home.getBanner()) {

                @Override
                public void onLeftClick(GUIButtonClickEvent event) throws SQLException, WorldNotExistException {
                    event.getWhoClicked().closeInventory();

                    if (home.getLocation().getBlock() != null && home.getLocation().getBlock().getType() == Material.BED_BLOCK) {

                        event.getWhoClicked().teleport(home.getLocation().clone().add(0.5, 0, 0.5));
                        Message.create("homeTeleport").param(color, home.getName()).sendTo(event.getWhoClicked());
                        if (data.isDead()) {
                            data.isDead(event.getWhoClicked(), false);
                            plugin.getPlayerStorage().update(data);
                        }
                        return;
                    }
                    Message.create("noBed").sendTo(event.getWhoClicked());
                }

                @Override
                public void onRightClick(GUIButtonClickEvent event) {
                    new NamingSession(event.getWhoClicked(), new NamingSession.NamingEventHandler() {
                        @Override
                        public void onName(NamingSession.NamingEvent event) throws SQLException {
                            if (event.getName().length() > 17) {
                                Message.create("homeNameMax").sendTo(player);
                                return;
                            }

                            final String originalName = home.getName();
                            home.setName(event.getName());
                            plugin.getHomesStorage().update(home);
                            Message.create("homeNameSet").param(originalName, home.getName()).sendTo(player);
                        }
                    });
                }

                @Override
                public void onShiftLeftClick(GUIButtonClickEvent event) {

                }

                @Override
                public void onShiftRightClick(GUIButtonClickEvent event) throws SQLException {
                    event.getWhoClicked().closeInventory();
                    plugin.getHomesStorage().remove(home);
                    Message.create("homeDelete").param(home.getName()).sendTo(event.getWhoClicked());
                }
            };

            homeButton.setDisplayName(ChatColor.translateAlternateColorCodes('&', color + home.getName()));
            homeButton.setLore(Arrays.asList("&6Left-Click: &fGo", "&6Right-Click: &fRename", "&6Shift+Right-Click: &fDelete"));
            set(size(), homeButton);
        }

        GUIButton backButton = new GUIButton(Material.BARRIER) {
            @Override
            public void action(GUIButtonClickEvent event) throws Exception {
                event.getWhoClicked().closeInventory();
                PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getWhoClicked());
                new NavigationMenu(data.getState(event.getWhoClicked())).open(event.getWhoClicked());
            }
        };

        backButton.setDisplayName(ChatColor.RED + "Back");
        set(getInventorySize() - 1, backButton);
    }


    @Override
    public void open(Player player) {
        player.openInventory(inventory);
    }

}
