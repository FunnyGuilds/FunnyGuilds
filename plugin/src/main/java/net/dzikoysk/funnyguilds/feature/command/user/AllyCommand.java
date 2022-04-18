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
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.utilities.text.Formatter;

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
            when(invitations.size() == 0, messages.allyHasNotInvitation);
            String guildNames = ChatUtils.toString(allyInvitationList.getInvitationGuildNames(guild), false);

            for (String msg : messages.allyInvitationList) {
                user.sendMessage(msg.replace("{GUILDS}", guildNames));
            }

            return;
        }

        Guild invitedGuild = GuildValidation.requireGuildByTag(args[0]);
        User invitedOwner = invitedGuild.getOwner();

        when(guild.equals(invitedGuild), messages.allySame);
        when(guild.isAlly(invitedGuild), messages.allyAlly);

        if (guild.isEnemy(invitedGuild)) {
            guild.removeEnemy(invitedGuild);

            String allyDoneMessage = messages.enemyEnd;
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{GUILD}", invitedGuild.getName());
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{TAG}", invitedGuild.getTag());
            user.sendMessage(allyDoneMessage);

            String allyIDoneMessage = messages.enemyIEnd;
            allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{GUILD}", guild.getName());
            allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{TAG}", guild.getTag());
            invitedOwner.sendMessage(allyIDoneMessage);
        }

        when(guild.getAllies().size() >= config.maxAlliesBetweenGuilds, () -> messages.inviteAllyAmount.replace("{AMOUNT}", Integer.toString(config.maxAlliesBetweenGuilds)));

        if (invitedGuild.getAllies().size() >= config.maxAlliesBetweenGuilds) {
            Formatter formatter = new Formatter()
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

            String allyDoneMessage = messages.allyDone;
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{GUILD}", invitedGuild.getName());
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{TAG}", invitedGuild.getTag());
            user.sendMessage(allyDoneMessage);

            String allyIDoneMessage = messages.allyIDone;
            allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{GUILD}", guild.getName());
            allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{TAG}", guild.getTag());
            invitedOwner.sendMessage(allyIDoneMessage);

            ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();

            for (User member : guild.getMembers()) {
                taskBuilder.delegate(new PrefixUpdateGuildRequest(member, invitedGuild));
            }

            for (User member : invitedGuild.getMembers()) {
                taskBuilder.delegate(new PrefixUpdateGuildRequest(member, guild));
            }

            this.concurrencyManager.postTask(taskBuilder.build());
            return;
        }

        if (allyInvitationList.hasInvitation(guild, invitedGuild)) {
            if (!SimpleEventHandler.handle(new GuildRevokeAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
                return;
            }

            allyInvitationList.expireInvitation(guild, invitedGuild);

            String allyReturnMessage = messages.allyReturn;
            allyReturnMessage = StringUtils.replace(allyReturnMessage, "{GUILD}", invitedGuild.getName());
            allyReturnMessage = StringUtils.replace(allyReturnMessage, "{TAG}", invitedGuild.getTag());
            user.sendMessage(allyReturnMessage);

            String allyIReturnMessage = messages.allyIReturn;
            allyIReturnMessage = StringUtils.replace(allyIReturnMessage, "{GUILD}", guild.getName());
            allyIReturnMessage = StringUtils.replace(allyIReturnMessage, "{TAG}", guild.getTag());
            invitedOwner.sendMessage(allyIReturnMessage);

            return;
        }

        if (!SimpleEventHandler.handle(new GuildSendAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
            return;
        }

        allyInvitationList.createInvitation(guild, invitedGuild);

        String allyInviteDoneMessage = messages.allyInviteDone;
        allyInviteDoneMessage = StringUtils.replace(allyInviteDoneMessage, "{GUILD}", invitedGuild.getName());
        allyInviteDoneMessage = StringUtils.replace(allyInviteDoneMessage, "{TAG}", invitedGuild.getTag());
        user.sendMessage(allyInviteDoneMessage);

        String allyToInvitedMessage = messages.allyToInvited;
        allyToInvitedMessage = StringUtils.replace(allyToInvitedMessage, "{GUILD}", guild.getName());
        allyToInvitedMessage = StringUtils.replace(allyToInvitedMessage, "{TAG}", guild.getTag());
        invitedOwner.sendMessage(allyToInvitedMessage);
    }

}
