package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class DeleteAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.delete.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, this.messages.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        User admin = AdminUtils.getAdminUser(sender);

        if (!SimpleEventHandler.handle(new GuildDeleteEvent(AdminUtils.getCause(admin), admin, guild))) {
            return;
        }

        this.guildManager.deleteGuild(this.plugin, guild);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", sender.getName())
                .register("{ADMIN}", sender.getName())
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        guild.getOwner().sendMessage(formatter.format(this.messages.adminGuildBroken));
        this.sendMessage(sender, formatter.format(this.messages.deleteSuccessful));
        this.broadcastMessage(formatter.format(this.messages.broadcastDelete));
    }

}
