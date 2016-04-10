package io.apollosoftware.naturalnavigation.menu.page;

import io.apollosoftware.lib.gui.GUIButton;
import io.apollosoftware.lib.gui.GUIButtonClickEvent;
import io.apollosoftware.lib.gui.GUIItem;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.menu.submenu.PlayersSubmenu;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;

/**
 * Class created by xenojava on 7/9/2015.
 */
public class PlayersPage extends Page<NaturalNavigation> {

    @Getter
    private int size = 0;

    public PlayersPage(PlayersSubmenu wrapper, int pageNum) {
        super(wrapper, wrapper.getInventory().getTitle() + " Page " + (pageNum + 2), pageNum);
    }

    @Override
    public void update() {
        GUIItem nextPage;
        if (wrapper.getPages().containsKey(next())) {
            nextPage = new GUIButton(Material.PAPER) {
                @Override
                public void action(GUIButtonClickEvent guiButtonClickEvent) {
                    wrapper.getPages().get(next()).open(guiButtonClickEvent.getWhoClicked());
                }
            };
            nextPage.setDisplayName(ChatColor.GREEN + "Next Page");
        } else {
            nextPage = getSpaceFillerItem();
        }

        set(26, nextPage);

        GUIItem prevPage;
        if (wrapper.getPages().containsKey(previous()) || pageNumber == 0) {
            prevPage = new GUIButton(Material.PAPER) {
                @Override
                public void action(GUIButtonClickEvent event) throws SQLException {

                    if (pageNumber == 0) {
                        PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getWhoClicked());
                        new PlayersSubmenu(wrapper.getInventory().getTitle(), data).open(event.getWhoClicked());
                        return;
                    }

                    wrapper.getPages().get(previous()).open(event.getWhoClicked());
                }
            };

            prevPage.setDisplayName(ChatColor.GREEN + "Previous Page");
        } else {
            prevPage = getSpaceFillerItem();
        }

        set(18, prevPage);

        fillEmptySpaces(getSpaceFillerItem());
        fillEmptySpaces(getAddFriendItem());
    }


    public void add(int slot, GUIItem item) {
        set(slot, item);
        size++;
    }

    public void remove(int slot) {
        size--;
        if (size <= 0) {
            remove();
            wrapper.getPages().remove(pageNumber);
        }
    }

    public int next() {
        return pageNumber + 1;
    }

    public int previous() {
        return pageNumber - 1;
    }


    @Override
    public void open(Player player) {
        update();
        player.openInventory(inventory);
    }

    @Override
    public boolean isFull() {
        return size >= CAPACITY;
    }


}
