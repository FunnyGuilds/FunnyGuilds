package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsMember;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

public class HelpRequestCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.helprequest.name}",
            description = "${user.helprequest.description}",
            aliases = "${user.helprequest.aliases}",
            permission = "funnyguilds.helprequest",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @IsMember User member) {
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", player.getName())
                .register("{X}", player.getLocation().getBlockX())
                .register("{Y}", player.getLocation().getBlockY())
                .register("{Z}", player.getLocation().getBlockZ());

        this.messageService.getMessage(config -> config.guild.commands.helpRequest)
                .with(formatter)
                .receiver(player)
                .send();
    }
}
