package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

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
    public void execute(Player player, @CanManage User user, Guild guild, String[] args) {
        if (args.length > 0) {
            when(!config.damageAlly, messages.generalAllyPvpDisabled);

            Guild targetAlliedGuild = GuildValidation.requireGuildByTag(args[0]);
            FunnyFormatter guildTagFormatter = FunnyFormatter.of("{TAG}", targetAlliedGuild.getTag());
            when(!guild.isAlly(targetAlliedGuild), guildTagFormatter.format(messages.allyDoesntExist));

            boolean newPvpValue = guild.toggleAllyPvP(targetAlliedGuild);
            user.sendMessage(guildTagFormatter.format(newPvpValue ? messages.pvpAllyOn : messages.pvpAllyOff));

            return;
        }

        boolean newPvpValue = guild.togglePvP();
        user.sendMessage(newPvpValue ? messages.pvpOn : messages.pvpOff);
    }

}
