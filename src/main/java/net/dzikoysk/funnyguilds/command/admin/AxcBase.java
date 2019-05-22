package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.dzikoysk.funnyguilds.util.commons.MessageFormatter;

public class AxcBase implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        
        if (args.length < 1) {
            sender.sendMessage(messages.generalNoNickGiven);
            return;
        }
        
        User user = UserUtils.get(args[0], true);
        if (user == null) {
            sender.sendMessage(messages.generalNotPlayedBefore);
            return;
        }
        
        if (!user.isOnline()) {
            sender.sendMessage(messages.generalNotOnline);
            return;
        }
        
        if (!user.hasGuild()) {
            sender.sendMessage(messages.generalPlayerHasNoGuild);
            return;
        }
        
        Location guildHome = user.getGuild().getHome();
        if (guildHome == null) {
            sender.sendMessage(messages.adminGuildHasNoHome);
            return;
        }
        
        Player targetPlayer = user.getPlayer();
        targetPlayer.teleport(guildHome);
        
        MessageFormatter formatter = new MessageFormatter().register("{ADMIN}", sender.getName()).register("{PLAYER}", targetPlayer.getName());
        targetPlayer.sendMessage(formatter.format(messages.adminTeleportedToBase));
        sender.sendMessage(formatter.format(messages.adminTargetTeleportedToBase));
    }

}
