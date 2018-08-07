package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import org.panda_lang.panda.utilities.commons.redact.MessageFormatter;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildUnbanEvent;
import net.dzikoysk.funnyguilds.system.ban.BanUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcUnban implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }

        Guild guild = GuildUtils.getByTag(args[0]);

        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }

        if (!guild.isBanned()) {
            sender.sendMessage(messages.adminGuildNotBanned);
            return;
        }
        
        User admin = (sender instanceof Player) ? User.get(sender.getName()) : null;
        if (!SimpleEventHandler.handle(new GuildUnbanEvent(admin == null ? EventCause.CONSOLE : EventCause.ADMIN, admin, guild))) {
            return;
        }

        BanUtils.unban(guild);

        MessageFormatter formatter = new MessageFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getName())
                .register("{ADMIN}", sender.getName());

        sender.sendMessage(formatter.format(messages.adminGuildUnban));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastUnban));
    }

}
