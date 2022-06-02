package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class ConfirmCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.confirm.name}",
            description = "${user.confirm.description}",
            aliases = "${user.confirm.aliases}",
            permission = "funnyguilds.delete",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @IsOwner User user, Guild guild) {
        when(config.guildDeleteCancelIfSomeoneIsOnRegion && regionManager.isAnyUserInRegion(guild.getRegion().orNull(),
                guild.getMembers()), messages.deleteSomeoneIsNear);
        when(!ConfirmationList.contains(user.getUUID()), messages.deleteToConfirm);

        ConfirmationList.remove(user.getUUID());

        if (!SimpleEventHandler.handle(new GuildDeleteEvent(EventCause.USER, user, guild))) {
            return;
        }

        this.guildManager.deleteGuild(plugin, guild);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", player.getName());

        sendMessage(player, formatter.format(messages.deleteSuccessful));
        broadcastMessage(formatter.format(messages.broadcastDelete));
    }

}
