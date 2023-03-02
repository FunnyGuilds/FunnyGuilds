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
        when(args.length < 1, config -> config.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when(args.length < 2, config -> config.generalNoNickGiven);

        User userToMove = UserValidation.requireUserByName(args[1]);
        when(!guild.isMember(userToMove), config -> config.adminUserNotMemberOf);

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildMemberDeputyEvent(AdminUtils.getCause(admin), admin, guild, userToMove))) {
            return;
        }

        FunnyFormatter formatter = FunnyFormatter.of("{PLAYER}", userToMove.getName());

        if (userToMove.isDeputy()) {
            guild.removeDeputy(userToMove);
            this.messageService.getMessage(config -> config.deputyRemove)
                    .receiver(sender)
                    .send();
            this.messageService.getMessage(config -> config.deputyMember)
                    .receiver(userToMove)
                    .send();
            this.messageService.getMessage(config -> config.deputyNoLongerMembers)
                    .with(formatter)
                    .receiver(guild)
                    .send();
            return;
        }

        guild.addDeputy(userToMove);

        this.messageService.getMessage(config -> config.deputySet)
                .receiver(sender)
                .send();
        this.messageService.getMessage( config -> config.deputyOwner)
                .receiver(userToMove)
                .send();
        this.messageService.getMessage(config -> config.deputyMembers)
                .with(formatter)
                .receiver(guild)
                .send();
    }

}
