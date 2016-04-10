package io.apollosoftware.naturalnavigation.menu.submenu;

import io.apollosoftware.naturalnavigation.data.WarpData;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.data.WarpData;
import io.apollosoftware.naturalnavigation.exception.WorldNotExistException;
import io.apollosoftware.naturalnavigation.menu.button.ActionButton;
import io.apollosoftware.naturalnavigation.menu.util.NamingSession;
import io.apollosoftware.lib.gui.GUIButton;
import io.apollosoftware.lib.gui.GUIButtonClickEvent;
import io.apollosoftware.lib.gui.Menu;
import io.apollosoftware.lib.lang.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class created by xenojava on 8/30/2015.
 */
public class WarpSubMenu extends Menu<NaturalNavigation> {

    private static List<WarpData> getWarps(NaturalNavigation plugin, PlayerData data) {
        final Player player = Bukkit.getPlayer(data.getUUID());
        return plugin.getWarpStorage().getWarps(plugin.getWorldGroup(player.getWorld().getName()));
    }

    public WarpSubMenu(final String title, final PlayerData data) {
        super(title, Menu.round(getWarps(NaturalNavigation.getPlugin(), data).size() + 1));
        setDestroyOnExit(true);

        final Player player = Bukkit.getPlayer(data.getUUID());

        if (player.hasPermission("nav.admin")) {
            GUIButton createButton = new GUIButton(Material.PAPER) {
                @Override
                public void action(final GUIButtonClickEvent event) throws Exception {
                    new WarpIconPickerMenu(event.getWhoClicked()).open(event.getWhoClicked());
                }
            };

            createButton.setDisplayName("&bCreate a warp.");
            set(getInventorySize() - 1, createButton);
        }

        for (final WarpData warp : getWarps(NaturalNavigation.getPlugin(), data)) {

            ActionButton warpButton = new ActionButton(warp.getWarpIcon()) {
                @Override
                public void onLeftClick(GUIButtonClickEvent event) throws SQLException, WorldNotExistException {
                    event.getWhoClicked().closeInventory();

                    event.getWhoClicked().teleport(warp.getLocation().clone().add(0.5, 0, 0.5));
                    Message.create("warpTeleport").param(warp.getName()).sendTo(event.getWhoClicked());

                    if (data.isDead()) {
                        data.isDead(event.getWhoClicked(), false);
                        plugin.getPlayerStorage().update(data);
                    }
                }

                @Override
                public void onRightClick(GUIButtonClickEvent event) {
                    if (!event.getWhoClicked().hasPermission("nav.admin")) return;

                    new NamingSession(event.getWhoClicked(), new NamingSession.NamingEventHandler() {
                        @Override
                        public void onName(NamingSession.NamingEvent event) throws SQLException {

                            if (event.getName().length() > 17) {
                                Message.create("warpNameMax").sendTo(player);
                                return;
                            }

                            final String originalName = warp.getName();
                            warp.setName(event.getName());
                            plugin.getWarpStorage().update(warp);
                            Message.create("warpNameSet").param(originalName, warp.getName()).sendTo(player);
                        }
                    });
                }

                @Override
                public void onShiftLeftClick(GUIButtonClickEvent event) {

                }

                @Override
                public void onShiftRightClick(GUIButtonClickEvent event) throws SQLException {
                    if (!event.getWhoClicked().hasPermission("nav.admin")) return;

                    event.getWhoClicked().closeInventory();
                    plugin.getWarpStorage().remove(warp);
                    Message.create("warpDelete").param(warp.getName()).sendTo(event.getWhoClicked());
                }
            };

            warpButton.setDisplayName(ChatColor.translateAlternateColorCodes('&', warp.getName()));

            List<String> lore = new ArrayList<>();

            if (player.hasPermission("nav.admin"))
                lore.addAll(Arrays.asList("&6Left-Click: &fGo", "&6Right-Click: &fRename", "&6Shift+Right-Click: &fDelete"));
            else lore.add("&6Left-Click: &fGo");

            warpButton.setLore(lore);
            add(warpButton);
        }

    }

    @Override
    public void open(final Player player) {
        player.openInventory(inventory);
    }


}
