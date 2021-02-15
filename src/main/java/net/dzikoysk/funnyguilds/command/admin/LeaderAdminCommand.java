package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.GuildValidation;
import net.dzikoysk.funnyguilds.command.UserValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaderEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class LeaderAdminCommand {

    @FunnyCommand(
        name = "${admin.leader.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length <1, messages.generalNoTagGiven);
        when (args.length < 2, messages.generalNoNickGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        User user = UserValidation.requireUserByName(args[1]);

        when (!guild.getMembers().contains(user), messages.adminUserNotMemberOf);
        when (guild.getOwner().equals(user), messages.adminAlreadyLeader);
        
        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildMemberLeaderEvent(AdminUtils.getCause(admin), admin, guild, user))) {
            return;
        }
        
        guild.setOwner(user);
        sender.sendMessage(messages.leaderSet);

        user.sendMessage(messages.leaderOwner);

        String message = messages.leaderMembers.replace("{PLAYER}", user.getName());

        for (User member : guild.getOnlineMembers()) {
            member.getPlayer().sendMessage(message);
        }
    }

}
