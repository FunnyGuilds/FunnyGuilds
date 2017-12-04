package net.dzikoysk.funnyguilds.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class ExcDeputy implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        User owner = User.get(player);

        if (!owner.hasGuild()) {
            player.sendMessage(messages.deputyHasNotGuild);
            return;
        }

        if (!owner.isOwner()) {
            player.sendMessage(messages.deputyIsNotOwner);
            return;
        }

        if (args.length < 1) {
            player.sendMessage(messages.deputyPlayer);
            return;
        }

        String name = args[0];
        
        if (!UserUtils.playedBefore(name)) {
            player.sendMessage(messages.deputyPlayedBefore);
            return;
        }

        User deputyUser = User.get(name);
        
        if (owner.equals(deputyUser)) {
            player.sendMessage(messages.deputyMustBeDifferent);
            return;
        }

        Guild guild = owner.getGuild();
        Player deputyPlayer = deputyUser.getPlayer();

        if (!guild.getMembers().contains(deputyUser)) {
            player.sendMessage(messages.deputyIsNotMember);
            return;
        }

        if (deputyUser.isDeputy()) {
            guild.setDeputy(null);
            player.sendMessage(messages.deputyRemove);
            
            if (deputyPlayer != null) {
                deputyPlayer.sendMessage(messages.deputyMember);
            }

            return;
        }

        guild.setDeputy(deputyUser);
        player.sendMessage(messages.deputySet);
        
        if (deputyPlayer != null) {
            deputyPlayer.sendMessage(messages.deputyOwner);
        }
    }

}
