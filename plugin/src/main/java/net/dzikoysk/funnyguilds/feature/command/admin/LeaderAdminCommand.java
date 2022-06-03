package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaderEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class LeaderAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.leader.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3 online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, this.messages.generalNoTagGiven);
        when(args.length < 2, this.messages.generalNoNickGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        User user = UserValidation.requireUserByName(args[1]);

        when(!guild.isMember(user), this.messages.adminUserNotMemberOf);
        when(guild.isOwner(user), this.messages.adminAlreadyLeader);

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildMemberLeaderEvent(AdminUtils.getCause(admin), admin, guild, user))) {
            return;
        }

        guild.setOwner(user);

        sendMessage(sender, this.messages.leaderSet);
        user.sendMessage(this.messages.leaderOwner);
        guild.broadcast(FunnyFormatter.formatOnce(this.messages.leaderMembers, "{PLAYER}", user.getName()));
    }

}
