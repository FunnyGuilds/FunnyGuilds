package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.Set;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixUpdateGuildRequest;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildAcceptAllyInvitationEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildRevokeAllyInvitationEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildSendAllyInvitationEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitation;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class AllyCommand extends AbstractFunnyCommand {

    @Inject
    public AllyInvitationList allyInvitationList;

    @FunnyCommand(
            name = "${user.ally.name}",
            description = "${user.ally.description}",
            aliases = "${user.ally.aliases}",
            permission = "funnyguilds.ally",
            completer = "guilds:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @IsOwner User user, Guild guild, String[] args) {
        Set<AllyInvitation> invitations = allyInvitationList.getInvitationsFor(guild);

        if (args.length < 1) {
            when(invitations.isEmpty(), messages.allyHasNotInvitation);
            String guildNames = ChatUtils.toString(allyInvitationList.getInvitationGuildNames(guild), false);

            FunnyFormatter formatter = new FunnyFormatter().register("{GUILDS}", guildNames);
            messages.allyInvitationList.forEach(line -> user.sendMessage(formatter.format(line)));

            return;
        }

        Guild invitedGuild = GuildValidation.requireGuildByTag(args[0]);
        User invitedOwner = invitedGuild.getOwner();

        when(guild.equals(invitedGuild), messages.allySame);
        when(guild.isAlly(invitedGuild), messages.allyAlly);

        if (guild.isEnemy(invitedGuild)) {
            guild.removeEnemy(invitedGuild);

            FunnyFormatter allyFormatter = new FunnyFormatter()
                    .register("{GUILD}", invitedGuild.getName())
                    .register("{TAG}", invitedGuild.getTag());

            FunnyFormatter allyIFormatter = new FunnyFormatter()
                    .register("{GUILD}", guild.getName())
                    .register("{TAG}", guild.getTag());

            user.sendMessage(allyFormatter.format(messages.enemyEnd));
            invitedOwner.sendMessage(allyIFormatter.format(messages.enemyIEnd));
        }

        when(guild.getAllies().size() >= config.maxAlliesBetweenGuilds, FunnyFormatter.formatOnce(messages.inviteAllyAmount,
                "{AMOUNT}", config.maxAlliesBetweenGuilds));

        if (invitedGuild.getAllies().size() >= config.maxAlliesBetweenGuilds) {
            FunnyFormatter formatter = new FunnyFormatter()
                    .register("{GUILD}", invitedGuild.getName())
                    .register("{TAG}", invitedGuild.getTag())
                    .register("{AMOUNT}", config.maxAlliesBetweenGuilds);

            user.sendMessage(formatter.format(messages.inviteAllyTargetAmount));
            return;
        }

        if (allyInvitationList.hasInvitation(invitedGuild, guild)) {
            if (!SimpleEventHandler.handle(new GuildAcceptAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
                return;
            }

            allyInvitationList.expireInvitation(invitedGuild, guild);

            guild.addAlly(invitedGuild);
            invitedGuild.addAlly(guild);

            FunnyFormatter allyFormatter = new FunnyFormatter()
                    .register("{GUILD}", invitedGuild.getName())
                    .register("{TAG}", invitedGuild.getTag());

            FunnyFormatter allyIFormatter = new FunnyFormatter()
                    .register("{GUILD}", guild.getName())
                    .register("{TAG}", guild.getTag());

            user.sendMessage(allyFormatter.format(messages.allyDone));
            invitedOwner.sendMessage(allyIFormatter.format(messages.allyIDone));

            ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();
            guild.getMembers().forEach(member -> taskBuilder.delegate(new PrefixUpdateGuildRequest(member, invitedGuild)));
            invitedGuild.getMembers().forEach(member -> taskBuilder.delegate(new PrefixUpdateGuildRequest(member, guild)));

            this.concurrencyManager.postTask(taskBuilder.build());
            return;
        }

        if (allyInvitationList.hasInvitation(guild, invitedGuild)) {
            if (!SimpleEventHandler.handle(new GuildRevokeAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
                return;
            }

            allyInvitationList.expireInvitation(guild, invitedGuild);

            FunnyFormatter allyFormatter = new FunnyFormatter()
                    .register("{GUILD}", invitedGuild.getName())
                    .register("{TAG}", invitedGuild.getTag());

            FunnyFormatter allyIFormatter = new FunnyFormatter()
                    .register("{GUILD}", guild.getName())
                    .register("{TAG}", guild.getTag());

            user.sendMessage(allyFormatter.format(messages.allyReturn));
            invitedOwner.sendMessage(allyIFormatter.format(messages.allyIReturn));

            return;
        }

        if (!SimpleEventHandler.handle(new GuildSendAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
            return;
        }

        allyInvitationList.createInvitation(guild, invitedGuild);

        FunnyFormatter allyFormatter = new FunnyFormatter()
                .register("{GUILD}", invitedGuild.getName())
                .register("{TAG}", invitedGuild.getTag());

        FunnyFormatter allyIFormatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        user.sendMessage(allyFormatter.format(messages.allyInviteDone));
        invitedOwner.sendMessage(allyIFormatter.format(messages.allyToInvited));
    }

}
