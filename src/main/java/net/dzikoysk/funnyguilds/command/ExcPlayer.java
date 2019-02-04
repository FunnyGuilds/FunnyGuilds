package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.Rank;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class ExcPlayer implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        
        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage(messages.playerOnly);
            return;
        }
        
        String name = args.length == 0 ? sender.getName() : args[0];

        if (!UserUtils.playedBefore(name, config.playerLookupIgnorecase)) {
            sender.sendMessage(messages.playerInfoExists);
            return;
        }

        User user = UserUtils.get(name, config.playerLookupIgnorecase);

        if (user == null) {
            sender.sendMessage(messages.playerInfoExists);
            return;
        }

        sendInfoMessage(messages.playerInfoList, user, sender);
    }
    
    public void sendInfoMessage(List<String> baseMessage, User infoUser, CommandSender messageTarget) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        
        Rank rank = infoUser.getRank();

        for (String messageLine : baseMessage) {
            if (infoUser.hasGuild()) {
                messageLine = StringUtils.replace(messageLine, "{GUILD}", infoUser.getGuild().getName());
                messageLine = StringUtils.replace(messageLine, "{TAG}", infoUser.getGuild().getTag());
            } else {
                messageLine = StringUtils.replace(messageLine, "{GUILD}", messages.gNameNoValue);
                messageLine = StringUtils.replace(messageLine, "{TAG}", messages.gTagNoValue);
            }

            messageLine = StringUtils.replace(messageLine, "{PLAYER}", infoUser.getName());
            messageLine = StringUtils.replace(messageLine, "{POINTS-FORMAT}", IntegerRange.inRange(rank.getPoints(), config.pointsFormat, "POINTS"));
            messageLine = StringUtils.replace(messageLine, "{POINTS}", Integer.toString(rank.getPoints()));
            messageLine = StringUtils.replace(messageLine, "{KILLS}", Integer.toString(rank.getKills()));
            messageLine = StringUtils.replace(messageLine, "{DEATHS}", Integer.toString(rank.getDeaths()));
            messageLine = StringUtils.replace(messageLine, "{KDR}", String.format(Locale.US, "%.2f", rank.getKDR()));
            messageLine = StringUtils.replace(messageLine, "{RANK}", Integer.toString(rank.getPosition()));
            
            messageTarget.sendMessage(messageLine);
        }
    }

}
