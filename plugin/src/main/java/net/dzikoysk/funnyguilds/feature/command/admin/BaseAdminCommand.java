package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.PositionConverter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class BaseAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.base.name}",
            permission = "funnyguilds.admin",
            completer = "online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.generalNoNickGiven);

        User userToTeleport = UserValidation.requireUserByName(args[0]);
        when(!userToTeleport.isOnline(), config -> config.generalNotOnline);
        when(!userToTeleport.hasGuild(), config -> config.generalPlayerHasNoGuild);

        Guild guild = userToTeleport.getGuild().get();
        when(!guild.hasHome(), config -> config.adminGuildHasNoHome);

        Location guildHome = guild.getHome().get();
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{ADMIN}", sender.getName())
                .register("{PLAYER}", userToTeleport.getName());

        userToTeleport.getProfile().teleport(PositionConverter.adapt(guildHome));
        this.messageService.getMessage(config -> config.adminTeleportedToBase)
                .with(formatter)
                .receiver(userToTeleport)
                .send();
        this.messageService.getMessage(config -> config.adminTargetTeleportedToBase)
                .with(formatter)
                .receiver(sender)
                .send();
    }

}
