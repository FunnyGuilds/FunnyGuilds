package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import org.panda_lang.panda.utilities.commons.redact.MessageFormatter;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcDelete implements Executor {

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

        User admin = (sender instanceof Player) ? User.get(sender.getName()) : null;
        if (!SimpleEventHandler.handle(new GuildDeleteEvent(admin == null ? EventCause.CONSOLE : EventCause.ADMIN, admin, guild))) {
            return;
        }
        
        GuildUtils.deleteGuild(guild);
        Player owner = guild.getOwner().getPlayer();

        MessageFormatter formatter = new MessageFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{ADMIN}", sender.getName())
                .register("{PLAYER}", sender.getName());

        if (owner != null) {
            owner.sendMessage(formatter.format(messages.adminGuildBroken));
        }

        sender.sendMessage(formatter.format(messages.deleteSuccessful));
        Bukkit.getServer().broadcastMessage(formatter.format(messages.broadcastDelete));
    }

}
