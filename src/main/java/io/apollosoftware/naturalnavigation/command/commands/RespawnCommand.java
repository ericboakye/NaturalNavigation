package io.apollosoftware.naturalnavigation.command.commands;

import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.command.NavigationCommand;

import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.SubCompartment;
import io.apollosoftware.lib.lang.Message;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

/**
 * Class created by xenojava on 9/5/2015.
 */
@PluginDependant
public class RespawnCommand extends SubCompartment<NavigationCommand> {

    private NaturalNavigation plugin;

    public RespawnCommand(NaturalNavigation plugin) {
        super("/nav respawn <player>", "respawn");
        this.plugin = plugin;
        this.setPermission("nav.command.respawn");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {

        if (args.length == 0) {
            sender.sendMessage(getUsage());
            return;
        }


        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            Message.create("notOnline").param(args[0]).sendTo(sender);
        }

        try {
            PlayerData data = plugin.getPlayerStorage().createIfNotExists(target);

            if (!data.isDead()) {
                Message.create("notDead").param(args[0]).sendTo(sender);
                return;
            }

            data.isDead(target, false);
            plugin.getPlayerStorage().update(data);
            Message.create("respawn").param(args[0]).sendTo(sender);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CommandException();
        }
    }


    public String[] getAliases() {
        return new String[0];
    }
}
