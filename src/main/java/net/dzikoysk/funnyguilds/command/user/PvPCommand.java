package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.CanManage;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.text.Formatter;

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
            if (!config.damageAlly) {
                player.sendMessage(messages.generalAllyPvpDisabled);
                return;
            }

            String alliedGuildTag = args[0];
            Guild targetAlliedGuild = GuildUtils.getByTag(alliedGuildTag);

            Formatter guildTagFormatter = new Formatter()
                    .register("{TAG}", alliedGuildTag);

            if (targetAlliedGuild == null) {
                player.sendMessage(guildTagFormatter.format(messages.generalGuildNotExists));
                return;
            }

            if (!guild.getAllies().contains(targetAlliedGuild)) {
                player.sendMessage(guildTagFormatter.format(messages.allyDoesntExist));
                return;
            }

            guild.setPvP(targetAlliedGuild, ! guild.getPvP(targetAlliedGuild));
            player.sendMessage(guildTagFormatter.format(guild.getPvP(targetAlliedGuild) ? messages.pvpAllyOn : messages.pvpAllyOff));
            return;
        }

        guild.setPvP(!guild.getPvP());
        player.sendMessage(guild.getPvP() ? messages.pvpOn : messages.pvpOff);
    }

}
