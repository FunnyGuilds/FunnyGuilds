package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class BreakCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.break.name}",
            description = "${user.break.description}",
            aliases = "${user.break.aliases}",
            permission = "funnyguilds.break",
            completer = "allies:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@HasGuildPermission(GuildCommandPermission.ALLY) User owner, Guild guild, String[] args) {
        when(!guild.hasAllies(), config -> config.breakHasNotAllies);

        if (args.length < 1) {
            this.messageService.getMessage(config -> config.breakAlliesList)
                    .receiver(owner)
                    .with("{GUILDS}", FunnyStringUtils.join(Entity.names(guild.getAllies()), true))
                    .send();
            return;
        }

        this.plugin.getGuildDiplomacyService().breakAlliance(owner, guild, GuildValidation.requireGuildByTag(args[0]));
    }

}
