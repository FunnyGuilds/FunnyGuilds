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

public class ExcPlayer implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig msg = Messages.getInstance();
        Player p = (Player) s;

        if (args.length == 0 && !(s instanceof Player)) {
            s.sendMessage(Messages.getInstance().playerOnly);
            return;
        }
        
        String name = p.getName();
        if (!UserUtils.playedBefore(name)) {
            s.sendMessage(msg.playerInfoExists);
            return;
        }

        User user = User.get(name);
        if (user.getUUID() == null) {
            s.sendMessage(msg.playerInfoExists);
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
            m = StringUtils.replace(m, "{RANK}", Integer.toString(RankManager.getInstance().getPosition(user)));
            
            s.sendMessage(m);
        }
    }
}
