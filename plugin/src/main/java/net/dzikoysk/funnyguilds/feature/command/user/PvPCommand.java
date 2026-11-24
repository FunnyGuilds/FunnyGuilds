package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

@FunnyComponent
public final class PvPCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.pvp.name}",
            description = "${user.pvp.description}",
            aliases = "${user.pvp.aliases}",
            permission = "funnyguilds.pvp",
            completer = "allies:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@HasGuildPermission(GuildCommandPermission.PVP) User deputy, Guild guild, String[] args) {
        if (args.length > 0) {
            this.plugin.getGuildDiplomacyService().toggleAllyPvp(deputy, guild, GuildValidation.requireGuildByTag(args[0]));
        } else {
            this.plugin.getGuildDiplomacyService().togglePvp(deputy, guild);
        }
    }

}
