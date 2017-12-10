package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class AxcDeputy implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        
        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }
        
        if (!GuildUtils.tagExists(args[0])) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage(messages.generalNoNickGiven);
            return;
        }
        
        if (!UserUtils.playedBefore(args[1])) {
            sender.sendMessage(messages.generalNotPlayedBefore);
            return;
        }
        
        Guild guild = GuildUtils.byTag(args[0]);
        User user = User.get(args[1]);
        
        if (!guild.getMembers().contains(user)) {
            sender.sendMessage(messages.adminUserNotMemberOf);
            return;
        }
        
        Player player = user.getPlayer();
        if (user.isDeputy()) {
            guild.setDeputy(null);
            sender.sendMessage(messages.deputyRemove);
            
            if (player != null) {
                player.sendMessage(messages.deputyMember);
            }
            
            for (User member : guild.getOnlineMembers()) {
                member.getPlayer().sendMessage(messages.deputyNoLongerMembers.replace("{PLAYER}", user.getName()));
            }

            return;
        }

        guild.setDeputy(user);
        sender.sendMessage(messages.deputySet);
        
        if (player != null) {
            player.sendMessage(messages.deputyOwner);
        }
        
        for (User member : guild.getOnlineMembers()) {
            member.getPlayer().sendMessage(messages.deputyMembers.replace("{PLAYER}", user.getName()));
        }
    }
}
