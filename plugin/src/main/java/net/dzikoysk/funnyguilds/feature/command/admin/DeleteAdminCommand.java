package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
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
        when(args.length < 1, messages.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        User admin = AdminUtils.getAdminUser(sender);

        if (!SimpleEventHandler.handle(new GuildDeleteEvent(AdminUtils.getCause(admin), admin, guild))) {
            return;
        }

        this.guildManager.deleteGuild(plugin, guild);

        FunnyFormatter formatter = guildPlaceholdersService.getSimplePlaceholders()
                .map(placeholders -> placeholders
                        .property("admin", sender::getName)
                        .property("player", sender::getName)
                )
                .map(placeholders -> placeholders.toVariablesFormatter(guild))
                .orElseGet(new FunnyFormatter());

        guild.getOwner().sendMessage(formatter.format(messages.adminGuildBroken));
        sendMessage(sender, formatter.format(messages.deleteSuccessful));
        Bukkit.getServer().broadcastMessage(formatter.format(messages.broadcastDelete));
    }

}
