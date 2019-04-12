package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.RankUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import org.bukkit.command.CommandSender;

public class ExcRanking implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String messageLine : FunnyGuilds.getInstance().getMessageConfiguration().rankingList) {
            String parsedRank = RankUtils.parseRank(null, messageLine);
            sender.sendMessage(parsedRank == null ? messageLine : parsedRank);
        }
    }

}
