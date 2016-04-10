package io.apollosoftware.naturalnavigation.command;

import io.apollosoftware.naturalnavigation.command.commands.DtpCommand;
import io.apollosoftware.naturalnavigation.command.commands.TpNoCommand;
import io.apollosoftware.naturalnavigation.command.commands.TpYesCommand;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.command.commands.DtpCommand;
import io.apollosoftware.naturalnavigation.command.commands.TpNoCommand;
import io.apollosoftware.naturalnavigation.command.commands.TpYesCommand;
import io.apollosoftware.lib.command.CommandManager;


public class PluginCommandWrapper extends CommandManager<NaturalNavigation> {


    public PluginCommandWrapper() throws Exception {
        super("nav.admin");
        registerCommand(NavigationCommand.class);
        registerCommand(DtpCommand.class);
        registerCommand(TpYesCommand.class);
        registerCommand(TpNoCommand.class);
    }

    public static PluginCommandWrapper register() throws Exception {
        return new PluginCommandWrapper();
    }

}
