package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.CanManage;
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
    public void execute(MessageConfiguration messages, Player player, @CanManage User user, Guild guild, String[] args) {
        if (args.length < 1) {
            player.sendMessage(messages.generalNoNickGiven);
            return;
        }

        User formerUser = User.get(args[0]);

        if (formerUser == null) {
            player.sendMessage(messages.generalNotPlayedBefore);
            return;
        }

        if (!formerUser.hasGuild()) {
            player.sendMessage(messages.generalPlayerHasNoGuild);
            return;
        }

        if (!guild.equals(formerUser.getGuild())) {
            player.sendMessage(messages.kickOtherGuild);
            return;
        }

        if (formerUser.isOwner()) {
            player.sendMessage(messages.kickOwner);
            return;
        }

        if (!SimpleEventHandler.handle(new GuildMemberKickEvent(EventCause.USER, user, guild, formerUser))) {
            return;
        }
        
        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalRemovePlayerRequest(formerUser.getName()));

        guild.removeMember(formerUser);
        formerUser.removeGuild();

        if (formerUser.isOnline()) {
            concurrencyManager.postRequests(new PrefixGlobalUpdatePlayer(player));
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
