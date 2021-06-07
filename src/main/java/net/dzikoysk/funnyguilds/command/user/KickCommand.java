package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.CanManage;
import net.dzikoysk.funnyguilds.command.UserValidation;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemovePlayerRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.text.Formatter;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.*;

@FunnyComponent
public final class KickCommand {

    @FunnyCommand(
        name = "${user.kick.name}",
        description = "${user.kick.description}",
        aliases = "${user.kick.aliases}",
        permission = "funnyguilds.kick",
        completer = "members:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(FunnyGuilds plugin, MessageConfiguration messages, Player player, @CanManage User user, Guild guild, String[] args) {
        when (args.length < 1, messages.generalNoNickGiven);

        User formerUser = UserValidation.requireUserByName(args[0]);
        when (!formerUser.hasGuild(), messages.generalPlayerHasNoGuild);
        when (!guild.equals(formerUser.getGuild()), messages.kickOtherGuild);
        when (formerUser.isOwner(), messages.kickOwner);

        if (!SimpleEventHandler.handle(new GuildMemberKickEvent(EventCause.USER, user, guild, formerUser))) {
            return;
        }
        
        ConcurrencyManager concurrencyManager = plugin.getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalRemovePlayerRequest(formerUser.getName(), plugin));

        guild.removeMember(formerUser);
        formerUser.removeGuild();

        if (formerUser.isOnline()) {
            concurrencyManager.postRequests(new PrefixGlobalUpdatePlayer(plugin, player));
        }

        Formatter formatter = new Formatter()
                .register("{PLAYER}", formerUser.getName())
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        player.sendMessage(formatter.format(messages.kickToOwner));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastKick));

        Player formerPlayer = formerUser.getPlayer();

        if (formerPlayer != null) {
            formerPlayer.sendMessage(formatter.format(messages.kickToPlayer));
        }
    }

}
