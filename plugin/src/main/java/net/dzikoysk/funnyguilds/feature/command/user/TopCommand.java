package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@FunnyComponent
public final class TopCommand {

    @FunnyCommand(
        name = "${user.top.name}",
        description = "${user.top.description}",
        aliases = "${user.top.aliases}",
        permission = "funnyguilds.top",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender) {
        User user = sender instanceof Player
                ? User.get((Player) sender)
                : null;

        for (String messageLine : messages.topList) {
            String parsedRank = RankUtils.parseRank(user, messageLine);
            sender.sendMessage(parsedRank == null ? messageLine : parsedRank);
        }
    }

}
