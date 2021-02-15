package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.GuildValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildUnbanEvent;
import net.dzikoysk.funnyguilds.system.ban.BanUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.commons.text.Formatter;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class UnbanCommand {

    @FunnyCommand(
        name = "${admin.unban.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when(!guild.isBanned(), messages.adminGuildNotBanned);
        
        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildUnbanEvent(AdminUtils.getCause(admin), admin, guild))) {
            return;
        }

        BanUtils.unban(guild);

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getName())
                .register("{ADMIN}", sender.getName());

        sender.sendMessage(formatter.format(messages.adminGuildUnban));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastUnban));
    }

}
