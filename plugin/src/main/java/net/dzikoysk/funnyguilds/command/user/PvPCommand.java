package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.CanManage;
import net.dzikoysk.funnyguilds.command.GuildValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

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
