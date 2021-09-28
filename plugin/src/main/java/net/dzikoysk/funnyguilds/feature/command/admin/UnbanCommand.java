package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildUnbanEvent;
import net.dzikoysk.funnyguilds.feature.ban.BanUtils;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class UnbanCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${admin.unban.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when (args.length < 1, this.messageConfig.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when (!guild.isBanned(), this.messageConfig.adminGuildNotBanned);
        
        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildUnbanEvent(AdminUtils.getCause(admin), admin, guild))) {
            return;
        }

        BanUtils.unban(guild);

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getName())
                .register("{ADMIN}", sender.getName());

        sender.sendMessage(formatter.format(this.messageConfig.adminGuildUnban));
        Bukkit.broadcastMessage(formatter.format(this.messageConfig.broadcastUnban));
    }

}
