package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

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
    public void execute(@CanManage User deputy, Guild guild, String[] args) {
        if (args.length > 0) {
            when(!this.config.damageAlly, config -> config.generalAllyPvpDisabled);

            Guild targetAlliedGuild = GuildValidation.requireGuildByTag(args[0]);
            FunnyFormatter guildTagFormatter = FunnyFormatter.of("{TAG}", targetAlliedGuild.getTag());
            when(!guild.isAlly(targetAlliedGuild), config -> config.allyDoesntExist, guildTagFormatter);

            boolean newPvpValue = guild.toggleAllyPvP(targetAlliedGuild);
            this.messageService.getMessage(config -> newPvpValue ? config.pvpAllyOn : config.pvpAllyOff)
                    .with(guildTagFormatter)
                    .receiver(deputy)
                    .send();

            return;
        }

        boolean newPvpValue = guild.togglePvP();
        this.messageService.getMessage(config -> newPvpValue ? config.pvpOn : config.pvpOff)
                .receiver(deputy)
                .send();
    }

}
