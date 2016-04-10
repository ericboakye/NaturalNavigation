package io.apollosoftware.naturalnavigation.command.commands;

import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.enums.TeleportType;
import io.apollosoftware.naturalnavigation.menu.TeleportRequest;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.ServerCommand;
import io.apollosoftware.lib.lang.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

import static io.apollosoftware.naturalnavigation.menu.TeleportRequest.*;

/**
 * Class created by xenojava on 9/5/2015.
 */
@PluginDependant
public class TpYesCommand extends ServerCommand {

    private NaturalNavigation plugin;

    public TpYesCommand(NaturalNavigation plugin) {
        super("tpyes");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        TeleportRequest tr = Cooldown.getCooldown(player.getName(),
                getRequester(player.getName()));
        if (tr == null) {
            Message.create("noRequests").sendTo(player);
            System.out.print("null");
            return;
        }
        if (tr.isOver()) {
            Message.create("noRequests").sendTo(player);
            return;
        }

        Player requester = Bukkit.getServer().getPlayer(getRequester(player.getName()));

        if (requester == null) {
            Message.create("playerNotOnline").sendTo(player);
            return;
        }

        Message.create("requestAccept").sendTo(player);
        Message.create("requestAccept2").sendTo(requester);

        Cooldown.delete(player.getName(),
                getRequester(player.getName()));

        if (tr.getTeleportType() == TeleportType.GO) {
            try {
                PlayerData data = plugin.getPlayerStorage().createIfNotExists(requester);

                if (data.isDead()) {
                    data.isDead(requester, false);
                    plugin.getPlayerStorage().update(data);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new CommandException();
            }
            requester.teleport(player);
        } else {
            player.teleport(requester);
        }

    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
