package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemovePlayerRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaveEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsMember;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class LeaveCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.leave.name}",
            description = "${user.leave.description}",
            aliases = "${user.leave.aliases}",
            permission = "funnyguilds.leave",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @IsMember User user, Guild guild) {
        when(user.isOwner(), messages.leaveIsOwner);

        if (!SimpleEventHandler.handle(new GuildMemberLeaveEvent(EventCause.USER, user, guild, user))) {
            return;
        }

        guild.removeMember(user);
        user.removeGuild();

        this.concurrencyManager.postRequests(new PrefixGlobalRemovePlayerRequest(individualPrefixManager, user.getName()), new PrefixGlobalUpdatePlayer(individualPrefixManager, player));

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", user.getName());

        user.sendMessage(formatter.format(messages.leaveToUser));
        broadcastMessage(formatter.format(messages.broadcastLeave));
    }

}
