package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberDeputyEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class DeputyAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.deputy.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3 online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, this.messages.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when(args.length < 2, this.messages.generalNoNickGiven);

        User userToMove = UserValidation.requireUserByName(args[1]);
        when(!guild.isMember(userToMove), this.messages.adminUserNotMemberOf);

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildMemberDeputyEvent(AdminUtils.getCause(admin), admin, guild, userToMove))) {
            return;
        }

        FunnyFormatter formatter = FunnyFormatter.of("{PLAYER}", userToMove.getName());

        if (userToMove.isDeputy()) {
            guild.removeDeputy(userToMove);
            this.sendMessage(sender, this.messages.deputyRemove);
            userToMove.sendMessage(this.messages.deputyMember);

            guild.broadcast(formatter.format(this.messages.deputyNoLongerMembers));
            return;
        }

        guild.addDeputy(userToMove);

        this.sendMessage(sender, this.messages.deputySet);
        userToMove.sendMessage(this.messages.deputyOwner);
        guild.broadcast(formatter.format(this.messages.deputyMembers));
    }

}
