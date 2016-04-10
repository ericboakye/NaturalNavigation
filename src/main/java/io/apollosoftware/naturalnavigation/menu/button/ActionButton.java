package io.apollosoftware.naturalnavigation.menu.button;

import io.apollosoftware.lib.gui.GUIButton;
import io.apollosoftware.lib.gui.GUIButtonClickEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Class created by xenojava on 8/31/2015.
 */
public abstract class ActionButton extends GUIButton {

    public ActionButton(ItemStack item) {
        super(item);
    }

    @Override
    public void action(GUIButtonClickEvent event) throws Exception {
        ClickType type = event.getClickEvent().getClick();

        switch (type) {
            case LEFT:
                onLeftClick(event);
                break;
            case RIGHT:
                onRightClick(event);
                break;
            case SHIFT_LEFT:
                onShiftLeftClick(event);
                break;
            case SHIFT_RIGHT:
                onShiftRightClick(event);
                break;
        }
    }


    public abstract void onLeftClick(GUIButtonClickEvent event) throws Exception;

    public abstract void onRightClick(GUIButtonClickEvent event) throws Exception;

    public abstract void onShiftLeftClick(GUIButtonClickEvent event) throws Exception;

    public abstract void onShiftRightClick(GUIButtonClickEvent event) throws Exception;
}
