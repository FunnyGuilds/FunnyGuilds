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
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.StringUtils;

public class AxcBan implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        } else if (args.length < 2) {
            sender.sendMessage(messages.adminNoBanTimeGiven);
            return;
        } else if (args.length < 3) {
            sender.sendMessage(messages.adminNoReasonGiven);
            return;
        }

        Guild guild = GuildUtils.get(args[0]);

        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        } 
        
        if (guild.isBanned()) {
            sender.sendMessage(messages.adminGuildBanned);
            return;
        }

        long time = Parser.parseTime(args[1]);

        if (time < 1) {
            sender.sendMessage(messages.adminTimeError);
            return;
        }

        StringBuilder reasonBuilder = new StringBuilder();

        for (int i = 2; i < args.length; i++) {
            reasonBuilder.append(args[i]);
            reasonBuilder.append(" ");
        }
        
        String reason = reasonBuilder.toString();
        BanUtils.ban(guild, time, reason);

        MessageTranslator translator = new MessageTranslator()
                .register("{GUILD", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{TIME}", args[1])
                .register("{REASON}", StringUtils.colored(reason));

        sender.sendMessage(translator.translate(messages.adminGuildBan));
        Bukkit.broadcastMessage(translator.translate(messages.broadcastBan));
    }

}
