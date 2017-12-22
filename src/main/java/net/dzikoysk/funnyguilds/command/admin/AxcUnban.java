package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.data.util.MessageTranslator;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.system.ban.BanUtils;

public class AxcUnban implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);

        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }

        if (!guild.isBanned()) {
            sender.sendMessage(messages.adminGuildNotBanned);
            return;
        }

        BanUtils.unban(guild);

        MessageTranslator translator = new MessageTranslator()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getName())
                .register("{ADMIN}", sender.getName());

        sender.sendMessage(translator.translate(messages.adminGuildUnban));
        Bukkit.broadcastMessage(translator.translate(messages.broadcastUnban));
    }

}
