package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.whenNull;

public final class BaseAdminCommand {

    @FunnyCommand(
        name = "${admin.base.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoNickGiven);
        
        User userToTeleport = UserValidation.requireUserByName(args[0]);
        when (!userToTeleport.isOnline(), messages.generalNotOnline);
        when (!userToTeleport.hasGuild(), messages.generalPlayerHasNoGuild);
        
        Location guildHome = userToTeleport.getGuild().getHome();
        whenNull (guildHome, messages.adminGuildHasNoHome);

        Formatter formatter = new Formatter()
                .register("{ADMIN}", sender.getName())
                .register("{PLAYER}", userToTeleport.getName());

        userToTeleport.getPlayer().teleport(guildHome);
        userToTeleport.sendMessage(formatter.format(messages.adminTeleportedToBase));
        sender.sendMessage(formatter.format(messages.adminTargetTeleportedToBase));
    }

}
