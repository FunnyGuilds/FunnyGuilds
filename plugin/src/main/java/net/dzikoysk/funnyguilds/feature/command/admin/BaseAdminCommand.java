package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class BaseAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.base.name}",
            permission = "funnyguilds.admin",
            completer = "online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, messages.generalNoNickGiven);

        User userToTeleport = UserValidation.requireUserByName(args[0]);
        when(!userToTeleport.isOnline(), messages.generalNotOnline);
        when(!userToTeleport.hasGuild(), messages.generalPlayerHasNoGuild);
        Guild guild = userToTeleport.getGuild().get();

        when(!guild.hasHome(), messages.adminGuildHasNoHome);
        Location guildHome = guild.getHome().get();

        Formatter formatter = new Formatter()
                .register("{ADMIN}", sender.getName())
                .register("{PLAYER}", userToTeleport.getName());

        Player player = server.getPlayer(userToTeleport.getUUID());

        if (player != null) {
            player.teleport(guildHome);
        }

        userToTeleport.sendMessage(formatter.format(messages.adminTeleportedToBase));
        sendMessage(sender, formatter.format(messages.adminTargetTeleportedToBase));
    }

}
