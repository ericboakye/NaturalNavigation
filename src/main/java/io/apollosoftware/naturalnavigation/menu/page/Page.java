package io.apollosoftware.naturalnavigation.menu.page;

import com.bobacadodl.JSONChatLib.*;
import io.apollosoftware.lib.ServerPlugin;
import io.apollosoftware.lib.gui.*;
import io.apollosoftware.naturalnavigation.menu.submenu.PlayersSubmenu;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

/**
 * Class created by xenojava on 7/9/2015.
 */
public abstract class Page<T extends ServerPlugin> extends GUI {

    protected static final int CAPACITY = 54;
    protected T plugin;
    protected PlayersSubmenu wrapper;
    private int size;

    @Getter
    protected int pageNumber;

    @SuppressWarnings("unchecked")
    public Page(PlayersSubmenu wrapper, String name, int pageNumber) {
        super(name, 54, wrapper.getData().getUUID());
        this.pageNumber = pageNumber;

        this.size = 54;
        this.wrapper = wrapper;

        Class<T> clazz = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);

        Method method = null;
        try {
            method = clazz.getDeclaredMethod("getPlugin", null);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            plugin = (T) method.invoke(null, method.getParameterTypes());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void fillSpace(int slot1, int slot2) {
        for (int i = 0; i < size; i++)
            if ((i >= slot1 && i <= slot2)) set(i, getSpaceFillerItem());
    }

    protected void fillEmptySpaces(GUIItem item) {
        for (int i = 0; i < size; i++)
            if (!containsKey(i)) set(i, item);
    }

    public abstract void open(Player player);

    protected GUIItem getSpaceFillerItem() {
        GUIItem g = new GUIDisplayer(Material.STAINED_GLASS_PANE, (byte) 15);
        g.setDisplayName(" ");

        return g;
    }

    protected GUIItem getAddFriendItem() {
        GUIItem g = new GUIButton(Material.STAINED_GLASS_PANE, (byte) 1) {
            @Override
            public void action(GUIButtonClickEvent event) {
                event.getWhoClicked().closeInventory();

                JSONChatMessage message = new JSONChatMessage("C", JSONChatColor.YELLOW, null);
                JSONChatExtra click = new JSONChatExtra("lick here to add a friend (Press tab to view our database)", JSONChatColor.YELLOW, new ArrayList<JSONChatFormat>());
                message.addExtra(click);
                click.setClickEvent(JSONChatClickEventType.SUGGEST_COMMAND, "/friend add ");

                try {
                    message.sendToPlayer(event.getWhoClicked());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        g.setDisplayName(ChatColor.GREEN + "Click here to add a friend.");

        return g;
    }

    public abstract boolean isFull();

    public abstract void update();

    public abstract int next();

    public abstract int previous();
}
