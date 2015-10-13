package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.Parser;
import org.bukkit.command.CommandSender;

public class ExcTop implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String m : Messages.getInstance().getList("topList")) {
            String r = Parser.parseRank(m);
            if (r != null)
                m = r;
            sender.sendMessage(m);
        }
    }

}
