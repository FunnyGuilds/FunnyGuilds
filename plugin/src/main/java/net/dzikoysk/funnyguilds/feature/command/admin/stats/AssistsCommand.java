package net.dzikoysk.funnyguilds.feature.command.admin.stats;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.rank.AssistsChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.command.CommandSender;

public final class AssistsCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.assists.name}",
            permission = "funnyguilds.admin",
            completer = "stats-operations:3 online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        StatsCommand.execute(
                this.messageService, sender, args,
                UserRank::getAssists,
                UserRank::setAssists,
                AssistsChangeEvent::new,
                AssistsChangeEvent::getAssistsChange,
                config -> config.adminNoAssistsGiven,
                "{ASSISTS}",
                config -> config.adminAssistsChanged
        );
    }

}
