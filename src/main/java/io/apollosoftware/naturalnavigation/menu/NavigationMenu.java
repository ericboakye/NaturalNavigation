package io.apollosoftware.naturalnavigation.menu;

import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.enums.NavigationType;
import io.apollosoftware.naturalnavigation.enums.PlayerState;
import io.apollosoftware.naturalnavigation.enums.SubmenuType;

import io.apollosoftware.lib.gui.GUIButton;
import io.apollosoftware.lib.gui.GUIButtonClickEvent;
import io.apollosoftware.lib.gui.GUIDisplayer;
import io.apollosoftware.lib.gui.Menu;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Class created by xenojava on 8/30/2015.
 */
public class NavigationMenu extends Menu<NaturalNavigation> {

    @Getter
    private NavigationType type;

    @Getter
    private boolean enabled;


    @SuppressWarnings("unchecked")
    public NavigationMenu(PlayerState state) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        super("Natural Navigation", 9);
        setDestroyOnExit(true);

        NavigationType type = state.getCorrespondingType();
        ConfigurationSection section = plugin.getConfiguration().conf.getConfigurationSection("menus." + type.name().toLowerCase());

        this.type = type;
        this.enabled = section.getBoolean("enabled");

        if (!enabled) return;

        for (Map<String, Object> map : (List<Map<String, Object>>) section.get("submenus")) {
            String title = (String) map.get("title");
            String icon = (String) map.get("icon");

            try {
                // Creates the sub menu instance
                SubmenuType submenuType = SubmenuType.valueOf(map.get("type").toString().toUpperCase());
                Class<?> menuClass = submenuType.getMenuClass();

                switch (submenuType) {
                    case HOMES:
                    case WARPS:
                    case PLAYERS:
                        set(size(), new PrivateSubmenuButton(menuClass, submenuType, plugin.parse(icon), title));
                        continue;
                }

                Constructor declaredConstructor = menuClass.getDeclaredConstructor(String.class);
                Menu<NaturalNavigation> submenu = (Menu) declaredConstructor.newInstance(title);
                submenu.setDestroyOnExit(true);

                set(size(), new GlobalSubmenuButton(submenu, submenuType, plugin.parse(icon), title));

            } catch (IllegalArgumentException ignored) {

            }
        }

        GUIDisplayer madeByXeno = new GUIDisplayer(Material.PAPER);
        madeByXeno.setDisplayName("&bCopyright (c) 2016 APOLLOSOFTWARE.IO");
        set(8, madeByXeno);
    }

    @Override
    public void open(Player player) {
        if (enabled)
            player.openInventory(inventory);
    }

    public class PrivateSubmenuButton extends GUIButton {

        private Class<?> menuClass;
        private SubmenuType type;
        private String title;

        public PrivateSubmenuButton(Class<?> menuClass, SubmenuType type, ItemStack icon, String title) {
            super(icon);
            this.title = title;
            this.type = type;
            this.menuClass = menuClass;
            setDisplayName(title);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void action(final GUIButtonClickEvent event) throws Exception {
            final PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getWhoClicked());

            Constructor declaredConstructor = menuClass.getDeclaredConstructor(String.class, PlayerData.class);
            Menu<NaturalNavigation> submenu = (Menu<NaturalNavigation>) declaredConstructor.newInstance(title, data);

            if (event.getWhoClicked().hasPermission(type.getPermission()))
                submenu.open(event.getWhoClicked());
        }
    }


    public class GlobalSubmenuButton extends GUIButton {
        private Menu submenu;
        private SubmenuType type;

        public GlobalSubmenuButton(Menu submenu, SubmenuType type, ItemStack icon, String title) {
            super(icon);
            this.submenu = submenu;
            this.type = type;
            setDisplayName(title);
        }

        @Override
        public void action(final GUIButtonClickEvent event) throws Exception {
            if (event.getWhoClicked().hasPermission(type.getPermission()))
                submenu.open(event.getWhoClicked());
        }
    }
}
