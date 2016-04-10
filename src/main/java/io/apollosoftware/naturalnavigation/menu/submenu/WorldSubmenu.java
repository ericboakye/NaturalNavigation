package io.apollosoftware.naturalnavigation.menu.submenu;

import io.apollosoftware.naturalnavigation.menu.NavigationMenu;
import io.apollosoftware.naturalnavigation.menu.button.ActionButton;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.menu.NavigationMenu;
import io.apollosoftware.naturalnavigation.menu.button.ActionButton;
import io.apollosoftware.naturalnavigation.world.WorldGroup;
import io.apollosoftware.lib.gui.GUIButton;
import io.apollosoftware.lib.gui.GUIButtonClickEvent;
import io.apollosoftware.lib.gui.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Collections;

/**
 * Class created by xenojava on 8/30/2015.
 */
public class WorldSubmenu extends Menu<NaturalNavigation> {


    public WorldSubmenu(String title) {
        super(title, Menu.round(NaturalNavigation.getPlugin().getWorldGroups().size()));

        for (final WorldGroup group : plugin.getWorldGroups()) {

            ActionButton worldButton = new ActionButton(group.getIcon()) {
                @Override
                public void onLeftClick(GUIButtonClickEvent event) throws SQLException {
                    event.getWhoClicked().closeInventory();
                    World world = group.entrySet().iterator().next().getValue().getBukkitWorld();
                    event.getWhoClicked().teleport(world.getSpawnLocation());
                    PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getWhoClicked());

                    if (data.isDead()) {
                        data.isDead(event.getWhoClicked(), false);
                        plugin.getPlayerStorage().update(data);
                    }
                }

                @Override
                public void onRightClick(GUIButtonClickEvent event) {
                }

                @Override
                public void onShiftLeftClick(GUIButtonClickEvent event) {
                }

                @Override
                public void onShiftRightClick(GUIButtonClickEvent event) {
                }
            };
            worldButton.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f" + group.getTitle()));
            worldButton.setLore(Collections.singletonList("&6Left click: &fGo"));
            set(size(), worldButton);
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
