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
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.user.User;
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
    public void execute(@IsOwner User owner, Guild guild, String[] args) {
        Set<AllyInvitation> invitations = this.allyInvitationList.getInvitationsFor(guild);

        if (args.length < 1) {
            when(invitations.isEmpty(), this.messages.allyHasNotInvitation);
            String guildNames = FunnyStringUtils.join(this.allyInvitationList.getInvitationGuildNames(guild), true);

            FunnyFormatter formatter = new FunnyFormatter().register("{GUILDS}", guildNames);
            this.messages.allyInvitationList.forEach(line -> owner.sendMessage(formatter.format(line)));

            return;
        }

        Guild invitedGuild = GuildValidation.requireGuildByTag(args[0]);
        User invitedOwner = invitedGuild.getOwner();

        when(guild.equals(invitedGuild), this.messages.allySame);
        when(guild.isAlly(invitedGuild), this.messages.allyAlly);

        if (guild.isEnemy(invitedGuild)) {
            this.endWar(owner, invitedOwner, guild, invitedGuild);
        }

        when(guild.getAllies().size() >= this.config.maxAlliesBetweenGuilds,
                FunnyFormatter.format(this.messages.inviteAllyAmount, "{AMOUNT}", this.config.maxAlliesBetweenGuilds));

        if (invitedGuild.getAllies().size() >= this.config.maxAlliesBetweenGuilds) {
            FunnyFormatter formatter = new FunnyFormatter()
                    .register("{GUILD}", invitedGuild.getName())
                    .register("{TAG}", invitedGuild.getTag())
                    .register("{AMOUNT}", this.config.maxAlliesBetweenGuilds);

            owner.sendMessage(formatter.format(this.messages.inviteAllyTargetAmount));
            return;
        }

        if (this.allyInvitationList.hasInvitation(invitedGuild, guild)) {
            this.acceptInvitation(owner, invitedOwner, guild, invitedGuild);
            return;
        }

        if (this.allyInvitationList.hasInvitation(guild, invitedGuild)) {
            this.revokeInvitation(owner, invitedOwner, guild, invitedGuild);
            return;
        }

        this.invite(owner, invitedOwner, guild, invitedGuild);
    }

    private void endWar(User owner, User invitedOwner, Guild guild, Guild invitedGuild) {
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

    private void acceptInvitation(User owner, User invitedOwner, Guild guild, Guild invitedGuild) {
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

        guild.getMembers().forEach(member -> {
            taskBuilder.delegate(new PrefixUpdateGuildRequest(member, invitedGuild));
        });

        invitedGuild.getMembers().forEach(member -> {
            taskBuilder.delegate(new PrefixUpdateGuildRequest(member, guild));
        });

        this.concurrencyManager.postTask(taskBuilder.build());
    }

    private void revokeInvitation(User owner, User invitedOwner, Guild guild, Guild invitedGuild) {
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
    }

    private void invite(User owner, User invitedOwner, Guild guild, Guild invitedGuild) {
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
