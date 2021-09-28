package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import org.bukkit.command.CommandSender;

public final class GuildsEnabledCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${admin.guilds-enabled.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        this.pluginConfiguration.guildsEnabled = !this.pluginConfiguration.guildsEnabled;
        sender.sendMessage(this.pluginConfiguration.guildsEnabled ? this.messageConfiguration.adminGuildsEnabled : this.messageConfiguration.adminGuildsDisabled);
    }

}
