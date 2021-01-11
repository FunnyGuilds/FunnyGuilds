package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.text.Formatter;

public final class BaseAdminCommand {

    @FunnyCommand(
        name = "${admin.base.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true,
        playerOnly = true
    )
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
        
        Formatter formatter = new Formatter().register("{ADMIN}", sender.getName()).register("{PLAYER}", targetPlayer.getName());
        targetPlayer.sendMessage(formatter.format(messages.adminTeleportedToBase));
        sender.sendMessage(formatter.format(messages.adminTargetTeleportedToBase));
    }

}
