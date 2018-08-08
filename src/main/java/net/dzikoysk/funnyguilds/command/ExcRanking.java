package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.rank.RankUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import org.bukkit.command.CommandSender;

public class ExcRanking implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String messageLine : Messages.getInstance().rankingList) {
            String parsedRank = RankUtils.parseRank(messageLine);
            sender.sendMessage(parsedRank == null ? messageLine : parsedRank);
        }
    }

}
