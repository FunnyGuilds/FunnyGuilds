package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.feature.scoreboard.nametag.NameTagGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class KickAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.kick.name}",
            permission = "funnyguilds.admin",
            completer = "online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.generalNoTagGiven);

        User user = UserValidation.requireUserByName(args[0]);
        when(!user.hasGuild(), config -> config.generalPlayerHasNoGuild);
        when(user.isOwner(), config -> config.adminGuildOwner);

        Guild guild = user.getGuild().get();
        User admin = AdminUtils.getAdminUser(sender);

        if (!SimpleEventHandler.handle(new GuildMemberKickEvent(AdminUtils.getCause(admin), admin, guild, user))) {
            return;
        }

        guild.removeMember(user);
        user.removeGuild();
        this.plugin.getIndividualNameTagManager()
                .map(manager -> new NameTagGlobalUpdateUserSyncTask(manager, user))
                .peek(this.plugin::scheduleFunnyTasks);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", user.getName());

        this.messageService.getMessage(config -> config.kickToOwner)
                .receiver(sender)
                .with(formatter)
                .send();
        this.messageService.getMessage(config -> config.kickToPlayer)
                .receiver(user)
                .with(formatter)
                .send();
        this.messageService.getMessage(config -> config.broadcastKick)
                .broadcast()
                .with(formatter)
                .send();
    }

}
