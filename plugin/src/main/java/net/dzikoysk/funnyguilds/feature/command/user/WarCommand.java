package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class WarCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.war.name}",
            description = "${user.war.description}",
            aliases = "${user.war.aliases}",
            permission = "funnyguilds.war",
            completer = "guilds:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@HasGuildPermission(GuildCommandPermission.WAR) User owner, Guild guild, String[] args) {
        when(args.length < 1, config -> config.enemyCorrectUse);
        Guild enemyGuild = GuildValidation.requireGuildByTag(args[0]);

        if (args.length > 1 && args[1].equalsIgnoreCase("zakoncz")) {
            this.plugin.getGuildDiplomacyService().withdrawWar(owner, guild, enemyGuild);
        } else {
            this.plugin.getGuildDiplomacyService().declareWar(owner, guild, enemyGuild);
        }
    }

}
