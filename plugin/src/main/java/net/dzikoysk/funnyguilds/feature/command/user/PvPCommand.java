package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class PvPCommand {

    @FunnyCommand(
        name = "${user.pvp.name}",
        description = "${user.pvp.description}",
        aliases = "${user.pvp.aliases}",
        permission = "funnyguilds.pvp",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, @CanManage User user, Guild guild, String[] args) {
        if (args.length > 0) {
            when (!config.damageAlly, messages.generalAllyPvpDisabled);

            Guild targetAlliedGuild = GuildValidation.requireGuildByTag(args[0]);
            Formatter guildTagFormatter = new Formatter().register("{TAG}", targetAlliedGuild.getTag());
            when (!guild.getAllies().contains(targetAlliedGuild), guildTagFormatter.format(messages.allyDoesntExist));

            guild.setPvP(targetAlliedGuild, ! guild.getPvP(targetAlliedGuild));
            player.sendMessage(guildTagFormatter.format(guild.getPvP(targetAlliedGuild) ? messages.pvpAllyOn : messages.pvpAllyOff));
            return;
        }

        guild.setPvP(!guild.getPvP());
        player.sendMessage(guild.getPvP() ? messages.pvpOn : messages.pvpOff);
    }

}
