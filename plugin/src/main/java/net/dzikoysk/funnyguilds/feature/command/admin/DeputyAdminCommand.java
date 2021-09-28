package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberDeputyEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class DeputyAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${admin.deputy.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when (args.length < 1, this.messageConfiguration.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when (args.length < 2, this.messageConfiguration.generalNoNickGiven);
        
        User userToMove = UserValidation.requireUserByName(args[1]);
        when (!guild.getMembers().contains(userToMove), this.messageConfiguration.adminUserNotMemberOf);

        User admin = AdminUtils.getAdminUser(sender);

        if (!SimpleEventHandler.handle(new GuildMemberDeputyEvent(AdminUtils.getCause(admin), admin, guild, userToMove))) {
            return;
        }
        
        Formatter formatter = new Formatter().register("{PLAYER}", userToMove.getName());

        if (userToMove.isDeputy()) {
            guild.removeDeputy(userToMove);
            sender.sendMessage(this.messageConfiguration.deputyRemove);
            userToMove.sendMessage(this.messageConfiguration.deputyMember);

            String message = formatter.format(this.messageConfiguration.deputyNoLongerMembers);

            for (User member : guild.getOnlineMembers()) {
                member.getPlayer().sendMessage(message);
            }

            return;
        }

        guild.addDeputy(userToMove);
        sender.sendMessage(this.messageConfiguration.deputySet);
        userToMove.sendMessage(this.messageConfiguration.deputyOwner);

        String message = formatter.format(this.messageConfiguration.deputyMembers);

        for (User member : guild.getOnlineMembers()) {
            member.getPlayer().sendMessage(message);
        }
    }

}
