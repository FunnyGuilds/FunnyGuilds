package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import org.bukkit.entity.Player;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class TeleportCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.teleport.name}",
            permission = "funnyguilds.admin",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, String[] args) {
        when(!config.regionsEnabled, messages.regionsDisabled);
        when(args.length < 1, messages.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        when(!guild.hasRegion(), messages.adminNoRegionFound);
        Region region = guild.getRegion().get();
        when(!region.hasCenter(), messages.adminNoRegionFound);

        player.sendMessage(messages.baseTeleport);
        player.teleport(region.getCenter().get());
    }

}
