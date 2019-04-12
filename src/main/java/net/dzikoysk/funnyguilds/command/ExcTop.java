package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.RankUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcTop implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        User user = sender instanceof Player
                ? User.get((Player) sender)
                : null;

        for (String messageLine : FunnyGuilds.getInstance().getMessageConfiguration().topList) {
            String parsedRank = RankUtils.parseRank(user, messageLine);
            sender.sendMessage(parsedRank == null ? messageLine : parsedRank);
        }
    }

}
