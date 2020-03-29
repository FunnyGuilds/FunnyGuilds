package net.dzikoysk.funnyguilds.command.manager;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.text.MessageFormatter;

public class MxcPvP implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }


        Guild guild = user.getGuild();

        if (args.length > 0) {
            if (! config.damageAlly) {
                player.sendMessage(messages.generalAllyPvpDisabled);
                return;
            }

            String alliedGuildTag = args[0];
            Guild targetAlliedGuild = GuildUtils.getByTag(alliedGuildTag);

            MessageFormatter guildTagFormatter = new MessageFormatter()
                    .register("{TAG}", alliedGuildTag);

            if (targetAlliedGuild == null) {
                player.sendMessage(guildTagFormatter.format(messages.generalGuildNotExists));
                return;
            }

            if (! guild.getAllies().contains(targetAlliedGuild)) {
                player.sendMessage(guildTagFormatter.format(messages.allyDoesntExist));
                return;
            }

            guild.setPvP(targetAlliedGuild, ! guild.getPvP(targetAlliedGuild));
            player.sendMessage(guildTagFormatter.format(guild.getPvP(targetAlliedGuild) ? messages.pvpAllyOn : messages.pvpAllyOff));
            return;
        }

        if (guild.getPvP()) {
            guild.setPvP(false);
            player.sendMessage(messages.pvpOff);
        }
        else {
            guild.setPvP(true);
            player.sendMessage(messages.pvpOn);
        }
    }
}
