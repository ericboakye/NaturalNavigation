package io.apollosoftware.naturalnavigation.command.commands;

import io.apollosoftware.naturalnavigation.command.NavigationCommand;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.command.NavigationCommand;

import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.SubCompartment;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Class created by xenojava on 9/5/2015.
 */
@PluginDependant
public class ReloadCommand extends SubCompartment<NavigationCommand> {

    private NaturalNavigation plugin;


    public ReloadCommand(NaturalNavigation plugin) {
        super("/nav reload", "reload");
        this.plugin = plugin;
        this.setPermission("nav.command.reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        plugin.getConfiguration().reload();
        plugin.getLanguageConfiguration().reload();
        plugin.reload();

        sender.sendMessage(ChatColor.GREEN + "NaturalNavigation Plugin v" + plugin.getDescription().getVersion());
    }

}
