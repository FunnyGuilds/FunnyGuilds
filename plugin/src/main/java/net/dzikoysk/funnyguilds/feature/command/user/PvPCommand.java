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
            when(!this.config.damageAlly, this.messages.generalAllyPvpDisabled);

            Guild targetAlliedGuild = GuildValidation.requireGuildByTag(args[0]);
            FunnyFormatter guildTagFormatter = FunnyFormatter.of("{TAG}", targetAlliedGuild.getTag());
            when(!guild.isAlly(targetAlliedGuild), guildTagFormatter.format(this.messages.allyDoesntExist));

            boolean newPvpValue = guild.toggleAllyPvP(targetAlliedGuild);
            deputy.sendMessage(guildTagFormatter.format(newPvpValue ? this.messages.pvpAllyOn : this.messages.pvpAllyOff));

            return;
        }

        boolean newPvpValue = guild.togglePvP();
        deputy.sendMessage(newPvpValue ? this.messages.pvpOn : this.messages.pvpOff);
    }

}
