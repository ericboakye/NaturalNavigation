package io.apollosoftware.naturalnavigation.command.commands;

import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.SubCompartment;
import io.apollosoftware.lib.lang.Message;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.command.NavigationCommand;
import io.apollosoftware.naturalnavigation.data.HomeData;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

/**
 * Class created by xenojava on 9/5/2015.
 */
@PluginDependant
public class GoHomeCommand extends SubCompartment<NavigationCommand> {

    private NaturalNavigation plugin;

    public GoHomeCommand(NaturalNavigation plugin) {
        super("/nav gohome <index> [<player>]", "gohome");

        setPermission("nav.command.gohome");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {

        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(getUsage());
            return;
        }

        String targetString = null;

        if (args.length >= 2) {
            targetString = args[1];

        } else if (args.length == 1) {
            targetString = player.getName();
        }

        if (!isInt(args[0])) {
            Message.create("notNumber").param(args[0]).sendTo(sender);
            return;
        }

        Player target = Bukkit.getPlayer(targetString);

        if (target == null) {
            Message.create("notOnline").param(args[1]).sendTo(sender);
        }

        try {
            PlayerData data = plugin.getPlayerStorage().createIfNotExists(target);
            HomeData home = plugin.getHomesStorage().getHomes(data).get(Integer.parseInt(args[0]));
            player.teleport(home.getLocation().clone().add(0.5, 0, 0.5));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CommandException();
        } catch (IndexOutOfBoundsException ignored) {
            Message.create("noIndex").param(args[1]).sendTo(sender);
        }
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    public String[] getAliases() {
        return new String[0];
    }
}
