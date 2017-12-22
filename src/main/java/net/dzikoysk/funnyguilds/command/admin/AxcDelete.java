package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.data.util.MessageTranslator;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class AxcDelete implements Executor {

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

        GuildUtils.deleteGuild(guild);
        Player owner = guild.getOwner().getPlayer();

        MessageTranslator translator = new MessageTranslator()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{ADMIN}", sender.getName())
                .register("{PLAYER}", sender.getName());

        if (owner != null) {
            owner.sendMessage(translator.translate(messages.adminGuildBroken));
        }

        sender.sendMessage(translator.translate(messages.deleteSuccessful));
        Bukkit.getServer().broadcastMessage(translator.translate(messages.broadcastDelete));
    }

}
