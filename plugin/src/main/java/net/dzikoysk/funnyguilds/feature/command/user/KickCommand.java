package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemovePlayerRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class KickCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.kick.name}",
            description = "${user.kick.description}",
            aliases = "${user.kick.aliases}",
            permission = "funnyguilds.kick",
            completer = "members:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @CanManage User user, Guild guild, String[] args) {
        when(args.length < 1, messages.generalNoNickGiven);

        User formerUser = UserValidation.requireUserByName(args[0]);
        when(!formerUser.hasGuild(), messages.generalPlayerHasNoGuild);
        when(!guild.equals(formerUser.getGuild().get()), messages.kickOtherGuild);
        when(formerUser.isOwner(), messages.kickOwner);

        if (!SimpleEventHandler.handle(new GuildMemberKickEvent(EventCause.USER, user, guild, formerUser))) {
            return;
        }

        this.concurrencyManager.postRequests(new PrefixGlobalRemovePlayerRequest(userManager, formerUser.getName()));

        guild.removeMember(formerUser);
        formerUser.removeGuild();

        if (formerUser.isOnline()) {
            concurrencyManager.postRequests(new PrefixGlobalUpdatePlayer(player));
        }

        Formatter formatter = new Formatter()
                .register("{PLAYER}", formerUser.getName())
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        user.sendMessage(formatter.format(messages.kickToOwner));
        broadcastMessage(formatter.format(messages.broadcastKick));

        formerUser.sendMessage(formatter.format(messages.kickToPlayer));
    }

}
