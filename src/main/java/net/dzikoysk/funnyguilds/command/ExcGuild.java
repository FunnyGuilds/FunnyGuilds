package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import org.bukkit.command.CommandSender;

public class ExcGuild implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String line : Messages.getInstance().getList("helpList"))
            sender.sendMessage(line);
    }
}
