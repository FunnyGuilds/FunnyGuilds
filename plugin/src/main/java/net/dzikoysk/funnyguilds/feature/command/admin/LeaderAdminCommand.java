package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaderEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class LeaderAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.leader.name}",
            permission = "funnyguilds.admin",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, messages.generalNoTagGiven);
        when(args.length < 2, messages.generalNoNickGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        User user = UserValidation.requireUserByName(args[1]);

        when(!guild.getMembers().contains(user), messages.adminUserNotMemberOf);
        when(guild.getOwner().equals(user), messages.adminAlreadyLeader);

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
