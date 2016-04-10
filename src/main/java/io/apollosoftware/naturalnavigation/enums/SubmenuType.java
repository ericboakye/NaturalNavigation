package io.apollosoftware.naturalnavigation.enums;

import io.apollosoftware.naturalnavigation.menu.submenu.*;
import lombok.Getter;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * Class created by xenojava on 8/30/2015.
 */
public enum SubmenuType {

    WORLDS(WorldSubmenu.class, "nav.menu.worlds"),
    HOMES(HomeSubmenu.class, "nav.menu.homes"),
    PLAYERS(PlayersSubmenu.class, "nav.menu.players"),
    WARPS(WarpSubMenu.class, "nav.menu.warps"),
    RANDOM(RandomTPSubMenu.class, "nav.menu.random");


    private Class<?> menuClass;
    @Getter
    private Permission permission;

    private SubmenuType(Class<?> crateClass, String permission) {
        this.menuClass = crateClass;
        this.permission = new Permission(permission);
        this.permission.setDefault(PermissionDefault.OP);
    }

    public Class<?> getMenuClass() {
        return this.menuClass;
    }

    public static SubmenuType getType(String name) {
        for (SubmenuType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
