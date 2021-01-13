package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildBanEvent;
import net.dzikoysk.funnyguilds.system.ban.BanUtils;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.text.Formatter;

public final class BanCommand {

    @FunnyCommand(
        name = "${admin.ban.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }
        else if (args.length < 2) {
            sender.sendMessage(messages.adminNoBanTimeGiven);
            return;
        }
        else if (args.length < 3) {
            sender.sendMessage(messages.adminNoReasonGiven);
            return;
        }

        Guild guild = GuildUtils.getByTag(args[0]);

        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        } 
        
        if (guild.isBanned()) {
            sender.sendMessage(messages.adminGuildBanned);
            return;
        }

        long time = TimeUtils.parseTime(args[1]);

        if (time < 1) {
            sender.sendMessage(messages.adminTimeError);
            return;
        }

        StringBuilder reasonBuilder = new StringBuilder();

        for (int index = 2; index < args.length; index++) {
            reasonBuilder.append(args[index]);
            reasonBuilder.append(" ");
        }
        
        String reason = reasonBuilder.toString();
        User admin = (sender instanceof Player) ? User.get(sender.getName()) : null;
        
        if (!SimpleEventHandler.handle(new GuildBanEvent(admin == null ? EventCause.CONSOLE : EventCause.ADMIN, admin, guild, time, reason))) {
            return;
        }
        
        BanUtils.ban(guild, time, reason);

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{TIME}", args[1])
                .register("{REASON}", ChatUtils.colored(reason));

        sender.sendMessage(formatter.format(messages.adminGuildBan));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastBan));
    }

}
