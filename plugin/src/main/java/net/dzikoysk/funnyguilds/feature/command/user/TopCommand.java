package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import panda.std.stream.PandaStream;

@FunnyComponent
public final class TopCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.top.name}",
            description = "${user.top.description}",
            aliases = "${user.top.aliases}",
            permission = "funnyguilds.top",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        User targetUser = this.userManager.findByName(sender.getName()).orNull();

        PandaStream.of(this.messages.topList).forEach(line -> {
            String parsedRank = this.rankPlaceholdersService.format(line, targetUser);
            sendMessage(sender, (parsedRank == null ? line : parsedRank));
        });
    }

}
