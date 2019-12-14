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
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberDeputyEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.panda.utilities.commons.text.MessageFormatter;

public class AxcDeputy implements Executor {

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

        Player player = user.getPlayer();

        if (!guild.getMembers().contains(user)) {
            sender.sendMessage(messages.adminUserNotMemberOf);
            return;
        }
        
        User admin = (sender instanceof Player) ? User.get(sender.getName()) : null;
        if (!SimpleEventHandler.handle(new GuildMemberDeputyEvent(admin == null ? EventCause.CONSOLE : EventCause.ADMIN, admin, guild, user))) {
            return;
        }
        
        MessageFormatter formatter = new MessageFormatter()
                .register("{PLAYER}", user.getName());

        if (user.isDeputy()) {
            guild.removeDeputy(user);
            sender.sendMessage(messages.deputyRemove);
            
            if (player != null) {
                player.sendMessage(messages.deputyMember);
            }

            String message = formatter.format(messages.deputyNoLongerMembers);

            for (User member : guild.getOnlineMembers()) {
                member.getPlayer().sendMessage(message);
            }

            return;
        }

        guild.addDeputy(user);
        sender.sendMessage(messages.deputySet);
        
        if (player != null) {
            player.sendMessage(messages.deputyOwner);
        }

        String message = formatter.format(messages.deputyMembers);

        for (User member : guild.getOnlineMembers()) {
            member.getPlayer().sendMessage(message);
        }
    }

}
