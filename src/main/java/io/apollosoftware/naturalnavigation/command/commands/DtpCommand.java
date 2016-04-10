package io.apollosoftware.naturalnavigation.command.commands;

import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.data.PlayerData;

import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.ServerCommand;
import io.apollosoftware.lib.lang.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

/**
 * Class created by xenojava on 9/5/2015.
 */
@PluginDependant
public class DtpCommand extends ServerCommand {

    private NaturalNavigation plugin;

    public DtpCommand(NaturalNavigation plugin) {
        super("dtp", "nav.command.dtp");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {

        if (!(sender instanceof Player)) return;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/dtp [<player>] <toplayer>");
            return;
        }

        Player player = (Player) sender;

        String target = null;
        String teleporter = null;

        if (args.length >= 2) {
            teleporter = args[0];
            target = args[1];
        } else if (args.length == 1) {
            teleporter = player.getName();
            target = args[0];
        }

        if (Bukkit.getPlayer(target) == null) {
            Message.create("notOnline").param(target).sendTo(player);
            return;
        }

        if (Bukkit.getPlayer(teleporter) == null) {
            Message.create("notOnline").param(teleporter).sendTo(player);
            return;
        }

        try {
            PlayerData data = plugin.getPlayerStorage().createIfNotExists(Bukkit.getPlayer(target));

            if(data.getDeathLocation() == null){
                Message.create("noRecentDeath").param(target).sendTo(Bukkit.getPlayer(teleporter));
                return;
            }

            Message.create("dtp").param(target).sendTo(Bukkit.getPlayer(teleporter));
            Bukkit.getPlayer(teleporter).teleport(data.getDeathLocation());

        } catch (SQLException e) {
            e.printStackTrace();
            throw new CommandException();
        }
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
