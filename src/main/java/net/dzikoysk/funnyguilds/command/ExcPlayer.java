package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ExcPlayer implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        
        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage(Messages.getInstance().playerOnly);
            return;
        }
        
        String name = args.length == 0 ? sender.getName() : args[0];

        if (!UserUtils.playedBefore(name)) {
            sender.sendMessage(messages.playerInfoExists);
            return;
        }

        User user = User.get(name);

        if (user.getUUID() == null) {
            sender.sendMessage(messages.playerInfoExists);
            return;
        }

        for (String messageLine : messages.playerInfoList) {
            if (user.hasGuild()) {
                messageLine = StringUtils.replace(messageLine, "{GUILD}", user.getGuild().getName());
                messageLine = StringUtils.replace(messageLine, "{TAG}", user.getGuild().getTag());
            } else {
                messageLine = StringUtils.replace(messageLine, "{GUILD}", "Brak");
                messageLine = StringUtils.replace(messageLine, "{TAG}", "Brak");
            }
            
            messageLine = StringUtils.replace(messageLine, "{PLAYER}", user.getName());
            messageLine = StringUtils.replace(messageLine, "{POINTS}", Integer.toString(user.getRank().getPoints()));
            messageLine = StringUtils.replace(messageLine, "{KILLS}", Integer.toString(user.getRank().getKills()));
            messageLine = StringUtils.replace(messageLine, "{DEATHS}", Integer.toString(user.getRank().getDeaths()));
            messageLine = StringUtils.replace(messageLine, "{KDR}", String.format(Locale.US, "%.2f", user.getRank().getKDR()));
            messageLine = StringUtils.replace(messageLine, "{RANK}", Integer.toString(RankManager.getInstance().getPosition(user)));
            
            sender.sendMessage(messageLine);
        }
    }

}
