package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

@FunnyComponent
public final class TopGCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.topg.name}",
            description = "${user.topg.description}",
            aliases = "${user.topg.aliases}",
            permission = "funnyguilds.topg",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        this.messageService.getMessage(config -> config.topList)
                .receiver(sender)
                .with(CommandSender.class, receiver -> {
                    User targetUser = this.userManager.findByName(sender.getName()).orNull();
                    return this.rankPlaceholdersService.prepareReplacement(targetUser);
                })
                .send();
    }
}
