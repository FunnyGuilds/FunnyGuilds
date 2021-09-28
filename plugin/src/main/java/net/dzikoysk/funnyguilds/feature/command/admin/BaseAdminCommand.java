package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.whenNull;

public final class BaseAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${admin.base.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when (args.length < 1, this.messageConfiguration.generalNoNickGiven);
        
        User userToTeleport = UserValidation.requireUserByName(args[0]);
        when (!userToTeleport.isOnline(), this.messageConfiguration.generalNotOnline);
        when (!userToTeleport.hasGuild(), this.messageConfiguration.generalPlayerHasNoGuild);
        
        Location guildHome = userToTeleport.getGuild().getHome();
        whenNull (guildHome, this.messageConfiguration.adminGuildHasNoHome);

        Formatter formatter = new Formatter()
                .register("{ADMIN}", sender.getName())
                .register("{PLAYER}", userToTeleport.getName());

        userToTeleport.getPlayer().teleport(guildHome);
        userToTeleport.sendMessage(formatter.format(this.messageConfiguration.adminTeleportedToBase));
        sender.sendMessage(formatter.format(this.messageConfiguration.adminTargetTeleportedToBase));
    }

}
