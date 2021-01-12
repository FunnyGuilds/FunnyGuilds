package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemovePlayerRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.text.Formatter;

public final class LeaveCommand {

    @FunnyCommand(
        name = "${user.leave.name}",
        description = "${user.leave.description}",
        aliases = "${user.leave.aliases}",
        permission = "funnyguilds.leave",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(MessageConfiguration messages, Player player, User user) {
        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (user.isOwner()) {
            player.sendMessage(messages.leaveIsOwner);
            return;
        }

        Guild guild = user.getGuild();

        if (!SimpleEventHandler.handle(new GuildMemberLeaveEvent(EventCause.USER, user, guild, user))) {
            return;
        }
        
        guild.removeMember(user);
        user.removeGuild();

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalRemovePlayerRequest(user.getName()), new PrefixGlobalUpdatePlayer(player));

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", user.getName());

        player.sendMessage(formatter.format(messages.leaveToUser));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastLeave));
    }

}
