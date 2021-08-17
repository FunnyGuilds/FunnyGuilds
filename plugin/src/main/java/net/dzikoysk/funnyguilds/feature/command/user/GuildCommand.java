package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import org.bukkit.command.CommandSender;

@FunnyComponent
public final class GuildCommand {

    @FunnyCommand(
        name = "${user.guild.name}",
        description = "${user.guild.description}",
        aliases = "${user.guild.aliases}",
        permission = "funnyguilds.guild",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender) {
        messages.helpList.forEach(sender::sendMessage);
    }

}
