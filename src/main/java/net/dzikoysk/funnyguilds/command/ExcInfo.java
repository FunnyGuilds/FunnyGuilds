package net.dzikoysk.funnyguilds.command;

import java.util.Date;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.StringUtils;

public class ExcInfo implements Executor {
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig msg = Messages.getInstance();
        String tag = null;

        if (args.length > 0) {
            tag = args[0];
        } else if (sender instanceof Player) {
            User user = User.get((Player) sender);
            if (user.hasGuild()) {
                tag = user.getGuild().getTag();
            }
        }

        if (tag == null || tag.isEmpty()) {
            sender.sendMessage(msg.infoTag);
            return;
        }

        if (!GuildUtils.tagExists(tag)) {
            sender.sendMessage(msg.infoExists);
            return;
        }

        Guild guild = GuildUtils.byTag(tag);
        if (guild == null) {
            sender.sendMessage(msg.infoExists);
            return;
        }

        String validity = Settings.getConfig().dateFormat.format(new Date(guild.getValidity()));
        for (String m : msg.infoList) {
            m = StringUtils.replace(m, "{GUILD}", guild.getName());
            m = StringUtils.replace(m, "{TAG}", guild.getTag());
            m = StringUtils.replace(m, "{OWNER}", guild.getOwner().getName());
            m = StringUtils.replace(m, "{MEMBERS}", StringUtils.toString(UserUtils.getOnlineNames(guild.getMembers()), true));
            m = StringUtils.replace(m, "{POINTS}", Integer.toString(guild.getRank().getPoints()));
            m = StringUtils.replace(m, "{KILLS}", Integer.toString(guild.getRank().getKills()));
            m = StringUtils.replace(m, "{DEATHS}", Integer.toString(guild.getRank().getDeaths()));
            m = StringUtils.replace(m, "{KDR}", String.format(Locale.US, "%.2f", guild.getRank().getKDR()));
            m = StringUtils.replace(m, "{RANK}", Integer.toString(RankManager.getInstance().getPosition(guild)));
            m = StringUtils.replace(m, "{VALIDITY}", validity);
            m = StringUtils.replace(m, "{LIVES}", Integer.toString(guild.getLives()));
            
            if (guild.getAllies().size() > 0) {
                m = StringUtils.replace(m, "{ALLIES}", StringUtils.toString(GuildUtils.getNames(guild.getAllies()), true));
            } else {
                m = StringUtils.replace(m, "{ALLIES}", "Brak");
            }
            
            if (m.contains("<online>")) {
                String color = ChatColor.getLastColors(m.split("<online>")[0]);
                m = StringUtils.replace(m, "<online>", ChatColor.GREEN + "");
                m = StringUtils.replace(m, "</online>", color);
            }
            
            sender.sendMessage(m);
        }
    }
}
