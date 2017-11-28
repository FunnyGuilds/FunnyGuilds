package net.dzikoysk.funnyguilds.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class ExcLeader implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) sender;
        User owner = User.get(p);

        if (!owner.hasGuild()) {
            p.sendMessage(m.leaderHasNotGuild);
            return;
        }

        if (!owner.isOwner()) {
            p.sendMessage(m.leaderIsNotOwner);
            return;
        }

        if (args.length < 1) {
            p.sendMessage(m.leaderPlayer);
            return;
        }

        String name = args[0];
        if (!UserUtils.playedBefore(name)) {
            p.sendMessage(m.leaderPlayedBefore);
            return;
        }

        User user = User.get(name);
        if (owner.equals(user)) {
            p.sendMessage(m.leaderMustBeDifferent);
            return;
        }

        Guild guild = owner.getGuild();
        if (!guild.getMembers().contains(user)) {
            p.sendMessage(m.leaderIsNotMember);
            return;
        }

        guild.setOwner(user);
        p.sendMessage(m.leaderSet);
        
        Player o = user.getPlayer();
        if (o != null) {
            o.sendMessage(m.leaderOwner);
        }
    }
}
