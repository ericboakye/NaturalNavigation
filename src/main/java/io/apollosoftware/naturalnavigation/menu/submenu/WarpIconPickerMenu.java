package io.apollosoftware.naturalnavigation.menu.submenu;

import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.menu.button.WarpIcon;
import io.apollosoftware.naturalnavigation.menu.button.WarpIconButton;
import io.apollosoftware.lib.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Class created by xenojava on 9/5/2015.
 */
public class WarpIconPickerMenu extends Menu<NaturalNavigation> {

    public WarpIconPickerMenu(Player player) {
        super("Choose an icon", Menu.round(player.getInventory().getContents().length), player.getUniqueId());
        setDestroyOnExit(true);

        List<WarpIcon> alreadyAdded = new ArrayList<>();
        mainLoop:
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;

            for (WarpIcon icon : alreadyAdded)
                if (icon.getItemStack().getType().equals(item.getType()) && icon.getItemStack().getDurability() == item.getDurability())
                    continue mainLoop;

            WarpIconButton warpIcon = new WarpIconButton(item, player);
            set(size(), warpIcon);
            alreadyAdded.add(warpIcon);
        }

        alreadyAdded.clear();
    }

    @Override
    public void open(Player player) {
        player.openInventory(inventory);
    }
}
