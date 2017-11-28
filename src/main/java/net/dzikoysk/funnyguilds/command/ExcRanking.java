package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.Parser;
import org.bukkit.command.CommandSender;

public class ExcRanking implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        for (String m : Messages.getInstance().rankingList) {
            String r = Parser.parseRank(m);
            s.sendMessage(r == null ? m : r);
        }
    }
}
