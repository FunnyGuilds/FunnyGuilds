package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaderEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcLeader implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        
        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }

        Guild guild = GuildUtils.getByTag(args[0]);
        
        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage(messages.generalNoNickGiven);
            return;
        }
        
        User user = User.get(args[1]);

        if (user == null) {
            sender.sendMessage(messages.generalNotPlayedBefore);
            return;
        }
        
        if (!guild.getMembers().contains(user)) {
            sender.sendMessage(messages.adminUserNotMemberOf);
            return;
        }
        
        if (guild.getOwner().equals(user)) {
            sender.sendMessage(messages.adminAlreadyLeader);
            return;
        }
        
        User admin = (sender instanceof Player) ? User.get(sender.getName()) : null;
        if (!SimpleEventHandler.handle(new GuildMemberLeaderEvent(admin == null ? EventCause.CONSOLE : EventCause.ADMIN, admin, guild, user))) {
            return;
        }
        
        Player leaderPlayer = user.getPlayer();
        guild.setOwner(user);
        sender.sendMessage(messages.leaderSet);

        if (leaderPlayer != null) {
            leaderPlayer.sendMessage(messages.leaderOwner);
        }

        String message = messages.leaderMembers.replace("{PLAYER}", user.getName());

        for (User member : guild.getOnlineMembers()) {
            member.getPlayer().sendMessage(message);
        }
    }

}
