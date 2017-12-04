package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.StringUtils;

import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcPlayer implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig msg = Messages.getInstance();
        
        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage(Messages.getInstance().playerOnly);
            return;
        }
        
        String name = args.length == 0 ? ((Player) sender).getName() : args[0];
        if (!UserUtils.playedBefore(name)) {
            sender.sendMessage(msg.playerInfoExists);
            return;
        }

        User user = User.get(name);
        if (user.getUUID() == null) {
            sender.sendMessage(msg.playerInfoExists);
            return;
        }

        for (String m : msg.playerInfoList) {
            if (user.hasGuild()) {
                m = StringUtils.replace(m, "{GUILD}", user.getGuild().getName());
                m = StringUtils.replace(m, "{TAG}", user.getGuild().getTag());
            } else {
                m = StringUtils.replace(m, "{GUILD}", "Brak");
                m = StringUtils.replace(m, "{TAG}", "Brak");
            }
            
            m = StringUtils.replace(m, "{PLAYER}", user.getName());
            m = StringUtils.replace(m, "{POINTS}", Integer.toString(user.getRank().getPoints()));
            m = StringUtils.replace(m, "{KILLS}", Integer.toString(user.getRank().getKills()));
            m = StringUtils.replace(m, "{DEATHS}", Integer.toString(user.getRank().getDeaths()));
            m = StringUtils.replace(m, "{KDR}", String.format(Locale.US, "%.2f", user.getRank().getKDR()));
            m = StringUtils.replace(m, "{RANK}", Integer.toString(RankManager.getInstance().getPosition(user)));
            
            sender.sendMessage(m);
        }
    }
}
