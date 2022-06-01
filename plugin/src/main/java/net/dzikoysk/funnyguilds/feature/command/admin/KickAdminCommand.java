package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemovePlayerRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class KickAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.kick.name}",
            permission = "funnyguilds.admin",
            completer = "online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, messages.generalNoTagGiven);

        User user = UserValidation.requireUserByName(args[0]);
        when(!user.hasGuild(), messages.generalPlayerHasNoGuild);
        when(user.isOwner(), messages.adminGuildOwner);

        Guild guild = user.getGuild().get();
        User admin = AdminUtils.getAdminUser(sender);

        if (!SimpleEventHandler.handle(new GuildMemberKickEvent(AdminUtils.getCause(admin), admin, guild, user))) {
            return;
        }

        this.concurrencyManager.postRequests(new PrefixGlobalRemovePlayerRequest(individualPrefixManager, user.getName()));

        guild.removeMember(user);
        user.removeGuild();

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", user.getName());

        sendMessage(sender, formatter.format(messages.kickToOwner));
        broadcastMessage(formatter.format(messages.broadcastKick));
        user.sendMessage(formatter.format(messages.kickToPlayer));

        this.funnyServer.getPlayer(user.getUUID())
                .peek(player -> this.concurrencyManager.postRequests(new PrefixGlobalUpdatePlayer(individualPrefixManager, player)));
    }

}
