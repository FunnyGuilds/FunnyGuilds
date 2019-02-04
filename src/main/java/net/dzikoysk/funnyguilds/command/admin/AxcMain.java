package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.command.util.Executor;
import org.bukkit.command.CommandSender;

public class AxcMain implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String line : FunnyGuilds.getInstance().getMessageConfiguration().adminHelpList) {
            sender.sendMessage(line);
        }
    }

}
