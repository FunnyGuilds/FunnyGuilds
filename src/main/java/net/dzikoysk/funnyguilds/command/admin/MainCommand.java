package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.command.CommandSender;

public final class MainCommand {

    @FunnyCommand(
        name = "${admin.main.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        for (String line : FunnyGuilds.getInstance().getMessageConfiguration().adminHelpList) {
            sender.sendMessage(line);
        }
    }

}
