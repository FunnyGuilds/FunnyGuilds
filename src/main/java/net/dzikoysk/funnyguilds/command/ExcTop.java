package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.Parser;
import org.bukkit.command.CommandSender;

public class ExcTop implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String messageLine : Messages.getInstance().topList) {
            String parsedRank = Parser.parseRank(messageLine);
            sender.sendMessage(parsedRank == null ? messageLine : parsedRank);
        }
    }

}
