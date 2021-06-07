package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.GuildValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildBanEvent;
import net.dzikoysk.funnyguilds.system.ban.BanSystem;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.commons.text.Formatter;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class BanCommand {

    @FunnyCommand(
        name = "${admin.ban.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(FunnyGuilds plugin, MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoTagGiven);
        when (args.length < 2, messages.adminNoBanTimeGiven);
        when (args.length < 3, messages.adminNoReasonGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when (guild.isBanned(), messages.adminGuildBanned);

        long time = TimeUtils.parseTime(args[1]);
        when (time < 1, messages.adminTimeError);

        StringBuilder reasonBuilder = new StringBuilder();

        for (int index = 2; index < args.length; index++) {
            reasonBuilder.append(args[index]);
            reasonBuilder.append(" ");
        }
        
        String reason = reasonBuilder.toString();
        User admin = AdminUtils.getAdminUser(sender);
        BanSystem banSystem = plugin.getSystemManager().getBanSystem();

        if (!SimpleEventHandler.handle(new GuildBanEvent(AdminUtils.getCause(admin), admin, guild, time, reason))) {
            return;
        }

        banSystem.ban(guild, time, reason);

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{TIME}", args[1])
                .register("{REASON}", ChatUtils.colored(reason));

        sender.sendMessage(formatter.format(messages.adminGuildBan));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastBan));
    }

}
