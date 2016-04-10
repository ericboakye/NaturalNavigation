package io.apollosoftware.naturalnavigation.command.commands;

import io.apollosoftware.naturalnavigation.menu.TeleportRequest;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.ServerCommand;
import io.apollosoftware.lib.lang.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.apollosoftware.naturalnavigation.menu.TeleportRequest.Cooldown;

/**
 * Class created by xenojava on 9/5/2015.
 */
public class TpNoCommand extends ServerCommand {

    public TpNoCommand() {
        super("tpno");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {

        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        TeleportRequest tr = Cooldown.getCooldown(player.getName(),
                TeleportRequest.getRequester(player.getName()));
        if (tr == null) {
            Message.create("noRequests").sendTo(player);
            return;
        }
        if (tr.isOver()) {
            Message.create("noRequests").sendTo(player);
            return;
        }

        Player requester = Bukkit.getServer().getPlayer(TeleportRequest.getRequester(player.getName()));

        if (requester == null) {
            Message.create("playerNotOnline").sendTo(player);
            return;
        }

        Cooldown.delete(player.getName(),
                TeleportRequest.getRequester(player.getName()));

        Message.create("requestDenied").sendTo(player);
        Message.create("requestDenied2").sendTo(requester);
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
