package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.command.util.Executor;
import org.bukkit.command.CommandSender;

public class ExcGuild implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String line : FunnyGuilds.getInstance().getMessageConfiguration().helpList) {
            sender.sendMessage(line);
        }
    }

}
