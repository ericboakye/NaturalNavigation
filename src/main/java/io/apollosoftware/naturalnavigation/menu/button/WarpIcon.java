package io.apollosoftware.naturalnavigation.menu.button;


import io.apollosoftware.lib.gui.GUIButton;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * Class created by xenojava on 8/31/2015.
 */
public abstract class WarpIcon extends GUIButton {
    public WarpIcon(ItemStack item) {
        super(item.getType(), item.getDurability());
        setDisplayName(ChatColor.YELLOW + "Click here to select as icon");
    }
}
