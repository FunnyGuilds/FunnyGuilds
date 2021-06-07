package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.IsMember;
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

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

@FunnyComponent
public final class LeaveCommand {

    @FunnyCommand(
        name = "${user.leave.name}",
        description = "${user.leave.description}",
        aliases = "${user.leave.aliases}",
        permission = "funnyguilds.leave",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(FunnyGuilds plugin, MessageConfiguration messages, Player player, @IsMember User user, Guild guild) {
        when (user.isOwner(), messages.leaveIsOwner);

        if (!SimpleEventHandler.handle(new GuildMemberLeaveEvent(EventCause.USER, user, guild, user))) {
            return;
        }
        
        guild.removeMember(user);
        user.removeGuild();

        ConcurrencyManager concurrencyManager = plugin.getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalRemovePlayerRequest(user.getName(), plugin), new PrefixGlobalUpdatePlayer(plugin, player));

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", user.getName());

        player.sendMessage(formatter.format(messages.leaveToUser));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastLeave));
    }

}
