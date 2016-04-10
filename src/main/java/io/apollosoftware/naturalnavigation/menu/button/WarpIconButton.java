package io.apollosoftware.naturalnavigation.menu.button;

import io.apollosoftware.lib.gui.GUIButtonClickEvent;
import io.apollosoftware.lib.lang.Message;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.data.WarpData;
import io.apollosoftware.naturalnavigation.menu.util.NamingSession;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

/**
 * Class created by xenojava on 9/5/2015.
 */


public class WarpIconButton extends WarpIcon {


    private Player player;

    public WarpIconButton(ItemStack item, Player player) {
        super(item);
        this.player = player;
    }

    @Override
    public void action(GUIButtonClickEvent guiButtonClickEvent) throws Exception {
        new NamingSession(player, new NamingSession.NamingEventHandler() {
            @Override
            public void onName(final NamingSession.NamingEvent namingEvent) throws Exception {
                if (namingEvent.getName().length() > 17) {
                    Message.create("warpNameMax").sendTo(player);
                    return;
                }
                try {
                    WarpData warpData = NaturalNavigation.getPlugin().getWarpStorage().createWarp(namingEvent.getName(), WarpIconButton.this, player.getLocation());
                    Message.create("warpCreate").param(warpData.getName()).sendTo(player);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

