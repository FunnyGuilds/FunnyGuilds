package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import org.bukkit.command.CommandSender;

@FunnyComponent
public final class RankingCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${user.ranking.name}",
        description = "${user.ranking.description}",
        aliases = "${user.ranking.aliases}",
        permission = "funnyguilds.ranking",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        for (String messageLine : messageConfiguration.rankingList) {
            String parsedRank = RankUtils.parseRank(null, messageLine);
            sender.sendMessage(parsedRank == null ? messageLine : parsedRank);
        }
    }

}
