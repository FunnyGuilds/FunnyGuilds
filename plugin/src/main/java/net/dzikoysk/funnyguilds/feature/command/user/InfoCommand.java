package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.rank.Rank;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.IntegerRange;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.ObjectUtils;

import java.util.Date;
import java.util.Locale;

@FunnyComponent
public final class InfoCommand {

    @FunnyCommand(
        name = "${user.info.name}",
        description = "${user.info.description}",
        aliases = "${user.info.aliases}",
        permission = "funnyguilds.info",
        completer = "guilds:3",
        acceptsExceeded = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, CommandSender sender, String[] args) {
        String tag = Option.when(args.length > 0, () -> args[0])
                .orElse(() -> Option.when(sender instanceof Player, () -> ObjectUtils.cast(Player.class, sender))
                        .map(User::get)
                        .filter(User::hasGuild)
                        .map(User::getGuild)
                        .map(Guild::getTag))
                .orThrow(() -> new ValidationException(messages.infoTag));

        Guild guild = GuildValidation.requireGuildByTag(tag);
        String validity = config.dateFormat.format(new Date(guild.getValidity()));
        
        long now = System.currentTimeMillis();
        long protectionEndTime = guild.getProtection();

        for (String messageLine : messages.infoList) {
            messageLine = StringUtils.replace(messageLine, "{GUILD}", guild.getName());
            messageLine = StringUtils.replace(messageLine, "{TAG}", guild.getTag());
            messageLine = StringUtils.replace(messageLine, "{OWNER}", guild.getOwner().getName());
            messageLine = StringUtils.replace(messageLine, "{MEMBERS-ONLINE}", String.valueOf(guild.getOnlineMembers().size()));
            messageLine = StringUtils.replace(messageLine, "{MEMBERS-ALL}", String.valueOf(guild.getMembers().size()));
            messageLine = StringUtils.replace(messageLine, "{MEMBERS}", ChatUtils.toString(UserUtils.getOnlineNames(guild.getMembers()), true));
            messageLine = StringUtils.replace(messageLine, "{DEPUTIES}", guild.getDeputies().isEmpty() ? "Brak" : ChatUtils.toString(UserUtils.getNamesOfUsers(guild.getDeputies()), true));
            messageLine = StringUtils.replace(messageLine, "{REGION-SIZE}", config.regionsEnabled ? String.valueOf(guild.getRegion().getSize()) : messages.gRegionSizeNoValue);
            messageLine = StringUtils.replace(messageLine, "{GUILD-PROTECTION}", protectionEndTime < now ? "Brak" : TimeUtils.getDurationBreakdown(protectionEndTime - now));

            Rank rank = guild.getRank();
            messageLine = StringUtils.replace(messageLine, "{POINTS-FORMAT}", IntegerRange.inRangeToString(rank.getPoints(), config.pointsFormat));
            messageLine = StringUtils.replace(messageLine, "{POINTS}", Integer.toString(rank.getPoints()));
            messageLine = StringUtils.replace(messageLine, "{KILLS}", Integer.toString(rank.getKills()));
            messageLine = StringUtils.replace(messageLine, "{DEATHS}", Integer.toString(rank.getDeaths()));
            messageLine = StringUtils.replace(messageLine, "{ASSISTS}", Integer.toString(rank.getAssists()));
            messageLine = StringUtils.replace(messageLine, "{LOGOUTS}", Integer.toString(rank.getLogouts()));
            messageLine = StringUtils.replace(messageLine, "{KDR}", String.format(Locale.US, "%.2f", rank.getKDR()));
            messageLine = StringUtils.replace(messageLine, "{VALIDITY}", validity);
            messageLine = StringUtils.replace(messageLine, "{LIVES}", Integer.toString(guild.getLives()));
            
            if (guild.getMembers().size() >= config.minMembersToInclude) {
                messageLine = StringUtils.replace(messageLine, "{RANK}", String.valueOf(rank.getPosition()));
            }
            else {
                messageLine = StringUtils.replace(messageLine, "{RANK}", messages.minMembersToIncludeNoValue);
            }
            
            if (!guild.getAllies().isEmpty()) {
                messageLine = StringUtils.replace(messageLine, "{ALLIES}", ChatUtils.toString(GuildUtils.getNames(guild.getAllies()), true));
                messageLine = StringUtils.replace(messageLine, "{ALLIES-TAGS}", ChatUtils.toString(GuildUtils.getTags(guild.getAllies()), true));
            }
            else {
                messageLine = StringUtils.replace(messageLine, "{ALLIES}", messages.alliesNoValue);
                messageLine = StringUtils.replace(messageLine, "{ALLIES-TAGS}", messages.alliesNoValue);
            }

            if (!guild.getEnemies().isEmpty()) {
                messageLine = StringUtils.replace(messageLine, "{ENEMIES}", ChatUtils.toString(GuildUtils.getNames(guild.getEnemies()), true));
                messageLine = StringUtils.replace(messageLine, "{ENEMIES-TAGS}", ChatUtils.toString(GuildUtils.getTags(guild.getEnemies()), true));
            }
            else {
                messageLine = StringUtils.replace(messageLine, "{ENEMIES}", messages.enemiesNoValue);
                messageLine = StringUtils.replace(messageLine, "{ENEMIES-TAGS}", messages.enemiesNoValue);
            }
            
            if (messageLine.contains("<online>")) {
                String color = ChatColor.getLastColors(messageLine.split("<online>")[0]);
                messageLine = StringUtils.replace(messageLine, "<online>", ChatColor.GREEN + "");
                messageLine = StringUtils.replace(messageLine, "</online>", color);
            }
            
            sender.sendMessage(messageLine);
        }
    }

}
