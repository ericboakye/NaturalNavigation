package io.apollosoftware.naturalnavigation.command;

import io.apollosoftware.naturalnavigation.command.commands.DelHomeCommand;
import io.apollosoftware.naturalnavigation.menu.NavigationMenu;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.command.commands.*;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.menu.NavigationMenu;
import io.apollosoftware.lib.command.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Class created by xenojava on 9/5/2015.
 */
@PluginDependant
public class NavigationCommand extends ServerCommand implements SubExecutor, PluginDependable<NaturalNavigation> {

    private NaturalNavigation plugin;

    public NavigationCommand(NaturalNavigation plugin) {
        super("navigation");
        this.plugin = plugin;

        compartments.add(IsDeadCommand.class);
        compartments.add(HomeListCommand.class);
        compartments.add(ReloadCommand.class);
        compartments.add(GoHomeCommand.class);
        compartments.add(DelHomeCommand.class);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        SubCompartment[] subCompartments = plugin.getCommandWrapper().getCompartments(compartments, this);

        if (args.length > 0) {
            if (!check(subCompartments, sender, args))
                sender.sendMessage(plugin.getCommandWrapper().getUsages(subCompartments));
            return;
        }

        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;

        try {

            PlayerData data = plugin.getPlayerStorage().createIfNotExists(player);
            new NavigationMenu(data.getState(player)).open(player);

        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException | SQLException e) {
            e.printStackTrace();
            throw new CommandException();
        }
    }

    @Override
    public String[] getAliases() {
        return new String[]{"nav"};
    }

    @Override
    public NaturalNavigation getPlugin() {
        return plugin;
    }
}
