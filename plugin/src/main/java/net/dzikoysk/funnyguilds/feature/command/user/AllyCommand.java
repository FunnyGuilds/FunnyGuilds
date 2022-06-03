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
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.stream.PandaStream;

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
    public void execute(@IsOwner User owner, Guild guild, String[] args) {
        Set<AllyInvitation> invitations = this.allyInvitationList.getInvitationsFor(guild);

        if (args.length < 1) {
            when(invitations.isEmpty(), this.messages.allyHasNotInvitation);
            String guildNames = ChatUtils.toString(this.allyInvitationList.getInvitationGuildNames(guild), false);

            FunnyFormatter formatter = new FunnyFormatter().register("{GUILDS}", guildNames);
            PandaStream.of(this.messages.allyInvitationList).forEach(line -> owner.sendMessage(formatter.format(line)));

            return;
        }

        Guild invitedGuild = GuildValidation.requireGuildByTag(args[0]);
        User invitedOwner = invitedGuild.getOwner();

        when(guild.equals(invitedGuild), this.messages.allySame);
        when(guild.isAlly(invitedGuild), this.messages.allyAlly);

        if (guild.isEnemy(invitedGuild)) {
            guild.removeEnemy(invitedGuild);

            FunnyFormatter allyFormatter = new FunnyFormatter()
                    .register("{GUILD}", invitedGuild.getName())
                    .register("{TAG}", invitedGuild.getTag());

            FunnyFormatter allyIFormatter = new FunnyFormatter()
                    .register("{GUILD}", guild.getName())
                    .register("{TAG}", guild.getTag());

            owner.sendMessage(allyFormatter.format(this.messages.enemyEnd));
            invitedOwner.sendMessage(allyIFormatter.format(this.messages.enemyIEnd));
        }

        when(guild.getAllies().size() >= this.config.maxAlliesBetweenGuilds,
                FunnyFormatter.formatOnce(this.messages.inviteAllyAmount, "{AMOUNT}", this.config.maxAlliesBetweenGuilds));

        if (invitedGuild.getAllies().size() >= this.config.maxAlliesBetweenGuilds) {
            FunnyFormatter formatter = new FunnyFormatter()
                    .register("{GUILD}", invitedGuild.getName())
                    .register("{TAG}", invitedGuild.getTag())
                    .register("{AMOUNT}", this.config.maxAlliesBetweenGuilds);

            owner.sendMessage(formatter.format(this.messages.inviteAllyTargetAmount));
            return;
        }

        if (this.allyInvitationList.hasInvitation(invitedGuild, guild)) {
            if (!SimpleEventHandler.handle(new GuildAcceptAllyInvitationEvent(EventCause.USER, owner, guild, invitedGuild))) {
                return;
            }

            this.allyInvitationList.expireInvitation(invitedGuild, guild);

            guild.addAlly(invitedGuild);
            invitedGuild.addAlly(guild);

            FunnyFormatter allyFormatter = new FunnyFormatter()
                    .register("{GUILD}", invitedGuild.getName())
                    .register("{TAG}", invitedGuild.getTag());

            FunnyFormatter allyIFormatter = new FunnyFormatter()
                    .register("{GUILD}", guild.getName())
                    .register("{TAG}", guild.getTag());

            owner.sendMessage(allyFormatter.format(this.messages.allyDone));
            invitedOwner.sendMessage(allyIFormatter.format(this.messages.allyIDone));

            ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();

            PandaStream.of(guild.getMembers()).forEach(member -> {
                taskBuilder.delegate(new PrefixUpdateGuildRequest(member, invitedGuild));
            });

            PandaStream.of(invitedGuild.getMembers()).forEach(member -> {
                taskBuilder.delegate(new PrefixUpdateGuildRequest(member, guild));
            });

            this.concurrencyManager.postTask(taskBuilder.build());
            return;
        }

        if (this.allyInvitationList.hasInvitation(guild, invitedGuild)) {
            if (!SimpleEventHandler.handle(new GuildRevokeAllyInvitationEvent(EventCause.USER, owner, guild, invitedGuild))) {
                return;
            }

            this.allyInvitationList.expireInvitation(guild, invitedGuild);

            FunnyFormatter allyFormatter = new FunnyFormatter()
                    .register("{GUILD}", invitedGuild.getName())
                    .register("{TAG}", invitedGuild.getTag());

            FunnyFormatter allyIFormatter = new FunnyFormatter()
                    .register("{GUILD}", guild.getName())
                    .register("{TAG}", guild.getTag());

            owner.sendMessage(allyFormatter.format(this.messages.allyReturn));
            invitedOwner.sendMessage(allyIFormatter.format(this.messages.allyIReturn));

            return;
        }

        if (!SimpleEventHandler.handle(new GuildSendAllyInvitationEvent(EventCause.USER, owner, guild, invitedGuild))) {
            return;
        }

        this.allyInvitationList.createInvitation(guild, invitedGuild);

        FunnyFormatter allyFormatter = new FunnyFormatter()
                .register("{GUILD}", invitedGuild.getName())
                .register("{TAG}", invitedGuild.getTag());

        FunnyFormatter allyIFormatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        owner.sendMessage(allyFormatter.format(this.messages.allyInviteDone));
        invitedOwner.sendMessage(allyIFormatter.format(this.messages.allyToInvited));
    }

}
