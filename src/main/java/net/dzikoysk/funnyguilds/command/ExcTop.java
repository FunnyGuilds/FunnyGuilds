package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.RankUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import org.bukkit.command.CommandSender;

public class ExcTop implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String messageLine : FunnyGuilds.getInstance().getMessageConfiguration().topList) {
            String parsedRank = RankUtils.parseRank(messageLine);
            sender.sendMessage(parsedRank == null ? messageLine : parsedRank);
        }
    }

}
