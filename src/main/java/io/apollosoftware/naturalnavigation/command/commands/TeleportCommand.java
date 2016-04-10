package io.apollosoftware.naturalnavigation.command.commands;

import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.ServerCommand;
import org.bukkit.command.CommandSender;

/**
 * Class created by xenojava on 9/5/2015.
 */
@PluginDependant
public class TeleportCommand extends ServerCommand {

    private NaturalNavigation plugin;

    public TeleportCommand(NaturalNavigation plugin) {
        super("tp", "nav.command.tp");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        //TODO
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
