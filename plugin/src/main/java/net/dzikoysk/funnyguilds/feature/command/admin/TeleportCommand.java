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
        when (!this.pluginConfiguration.regionsEnabled, this.messageConfiguration.regionsDisabled);
        when (args.length < 1, this.messageConfiguration.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        Region region = guild.getRegion();
        when (region == null || region.getCenter() == null, this.messageConfiguration.adminNoRegionFound);

        player.sendMessage(this.messageConfiguration.baseTeleport);
        player.teleport(region.getCenter());
    }

}
