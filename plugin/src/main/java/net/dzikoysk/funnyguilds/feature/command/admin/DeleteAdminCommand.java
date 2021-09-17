package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class DeleteAdminCommand {

    @FunnyCommand(
        name = "${admin.delete.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        User admin = AdminUtils.getAdminUser(sender);

        if (!SimpleEventHandler.handle(new GuildDeleteEvent(AdminUtils.getCause(admin), admin, guild))) {
            return;
        }
        
        GuildUtils.deleteGuild(guild);

        Formatter formatter = Placeholders.GUILD
                .bracket("ADMIN", sender::getName)
                .bracket("PLAYER", sender::getName)
                .toFormatter(guild);

        guild.getOwner().sendMessage(formatter.format(messages.adminGuildBroken));
        sender.sendMessage(formatter.format(messages.deleteSuccessful));
        Bukkit.getServer().broadcastMessage(formatter.format(messages.broadcastDelete));
    }

}
