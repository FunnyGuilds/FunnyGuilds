package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.command.CommandSender;

public final class GuildCommand {

    @FunnyCommand(
        name = "${user.guild.name}",
        description = "${user.guild.description}",
        aliases = "${user.guild.aliases}",
        permission = "funnyguilds.guild",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        for (String line : FunnyGuilds.getInstance().getMessageConfiguration().helpList) {
            sender.sendMessage(line);
        }
    }

}
