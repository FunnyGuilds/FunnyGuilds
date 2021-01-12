package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.rank.RankUtils;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.bukkit.command.CommandSender;

public final class RankingCommand {

    @FunnyCommand(
        name = "${user.ranking.name}",
        description = "${user.ranking.description}",
        aliases = "${user.ranking.aliases}",
        permission = "funnyguilds.ranking",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender) {
        for (String messageLine : messages.rankingList) {
            String parsedRank = RankUtils.parseRank(null, messageLine);
            sender.sendMessage(parsedRank == null ? messageLine : parsedRank);
        }
    }

}
