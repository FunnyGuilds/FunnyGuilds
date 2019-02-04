package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.Rank;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.KillsChangeEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcKills implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoNickGiven);
            return;
        }
        
        if (!UserUtils.playedBefore(args[0])) {
            sender.sendMessage(messages.generalNotPlayedBefore);
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(messages.adminNoKillsGiven);
            return;
        }

        int kills;
        try {
            kills = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(messages.adminErrorInNumber.replace("{ERROR}", args[1]));
            return;
        }

        User user = User.get(args[0]);
        Rank userRank = user.getRank();
        
        int change = kills - userRank.getDeaths();
        User admin = (sender instanceof Player) ? User.get(sender.getName()) : null;
        if (!SimpleEventHandler.handle(new KillsChangeEvent(admin == null ? EventCause.CONSOLE : EventCause.ADMIN, userRank, admin, change))) {
            return;
        }
        
        user.getRank().setKills(kills);
        sender.sendMessage(messages.adminKillsChanged.replace("{PLAYER}", user.getName()).replace("{KILLS}", Integer.toString(kills)));
    }

}
