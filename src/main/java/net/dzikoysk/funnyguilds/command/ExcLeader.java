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
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        User owner = User.get(player);

        if (!owner.hasGuild()) {
            player.sendMessage(messages.leaderHasNotGuild);
            return;
        }

        if (!owner.isOwner()) {
            player.sendMessage(messages.leaderIsNotOwner);
            return;
        }

        if (args.length < 1) {
            player.sendMessage(messages.leaderPlayer);
            return;
        }

        String name = args[0];

        if (!UserUtils.playedBefore(name)) {
            player.sendMessage(messages.leaderPlayedBefore);
            return;
        }

        User leaderUser = User.get(name);

        if (owner.equals(leaderUser)) {
            player.sendMessage(messages.leaderMustBeDifferent);
            return;
        }

        Guild guild = owner.getGuild();

        if (!guild.getMembers().contains(leaderUser)) {
            player.sendMessage(messages.leaderIsNotMember);
            return;
        }

        Player leaderPlayer = leaderUser.getPlayer();
        guild.setOwner(leaderUser);
        player.sendMessage(messages.leaderSet);

        if (leaderPlayer != null) {
            leaderPlayer.sendMessage(messages.leaderOwner);
        }
    }

}
