package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixUpdateGuildRequest;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildAcceptAllyInvitationEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildRevokeAllyInvitationEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildSendAllyInvitationEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.util.List;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class AllyCommand extends AbstractFunnyCommand {

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
        List<InvitationList.Invitation> invitations = InvitationList.getInvitationsFor(guild);

        if (args.length < 1) {
            when (invitations.size() == 0, this.messageConfig.allyHasNotInvitation);
            String guildNames = ChatUtils.toString(InvitationList.getInvitationGuildNames(guild), false);

            for (String msg : this.messageConfig.allyInvitationList) {
                player.sendMessage(msg.replace("{GUILDS}", guildNames));
            }

            return;
        }

        Guild invitedGuild = GuildValidation.requireGuildByTag(args[0]);
        Player invitedOwner = invitedGuild.getOwner().getPlayer();

        when (guild.equals(invitedGuild), this.messageConfig.allySame);
        when (guild.getAllies().contains(invitedGuild), this.messageConfig.allyAlly);

        if (guild.getEnemies().contains(invitedGuild)) {
            guild.removeEnemy(invitedGuild);

            String allyDoneMessage = this.messageConfig.enemyEnd;
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{GUILD}", invitedGuild.getName());
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{TAG}", invitedGuild.getTag());
            player.sendMessage(allyDoneMessage);

            if (invitedOwner != null) {
                String allyIDoneMessage = this.messageConfig.enemyIEnd;
                allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{GUILD}", guild.getName());
                allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{TAG}", guild.getTag());
                invitedOwner.sendMessage(allyIDoneMessage);
            }
        }

        when (guild.getAllies().size() >= this.pluginConfig.maxAlliesBetweenGuilds, () -> this.messageConfig.inviteAllyAmount.replace("{AMOUNT}", Integer.toString(this.pluginConfig.maxAlliesBetweenGuilds)));

        if (invitedGuild.getAllies().size() >= this.pluginConfig.maxAlliesBetweenGuilds) {
            Formatter formatter = new Formatter()
                    .register("{GUILD}", invitedGuild.getName())
                    .register("{TAG}", invitedGuild.getTag())
                    .register("{AMOUNT}", this.pluginConfig.maxAlliesBetweenGuilds);

            player.sendMessage(formatter.format(this.messageConfig.inviteAllyTargetAmount));
            return;
        }


        if (InvitationList.hasInvitationFrom(guild, invitedGuild)) {
            if (!SimpleEventHandler.handle(new GuildAcceptAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
                return;
            }

            InvitationList.expireInvitation(invitedGuild, guild);

            guild.addAlly(invitedGuild);
            invitedGuild.addAlly(guild);

            String allyDoneMessage = this.messageConfig.allyDone;
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{GUILD}", invitedGuild.getName());
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{TAG}", invitedGuild.getTag());
            player.sendMessage(allyDoneMessage);

            if (invitedOwner != null) {
                String allyIDoneMessage = this.messageConfig.allyIDone;
                allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{GUILD}", guild.getName());
                allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{TAG}", guild.getTag());
                invitedOwner.sendMessage(allyIDoneMessage);
            }

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

        if (InvitationList.hasInvitationFrom(invitedGuild, guild)) {
            if (!SimpleEventHandler.handle(new GuildRevokeAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
                return;
            }

            InvitationList.expireInvitation(guild, invitedGuild);

            String allyReturnMessage = this.messageConfig.allyReturn;
            allyReturnMessage = StringUtils.replace(allyReturnMessage, "{GUILD}", invitedGuild.getName());
            allyReturnMessage = StringUtils.replace(allyReturnMessage, "{TAG}", invitedGuild.getTag());
            player.sendMessage(allyReturnMessage);


            if (invitedOwner != null) {
                String allyIReturnMessage = this.messageConfig.allyIReturn;
                allyIReturnMessage = StringUtils.replace(allyIReturnMessage, "{GUILD}", guild.getName());
                allyIReturnMessage = StringUtils.replace(allyIReturnMessage, "{TAG}", guild.getTag());
                invitedOwner.sendMessage(allyIReturnMessage);
            }

            return;
        }

        if (!SimpleEventHandler.handle(new GuildSendAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
            return;
        }

        InvitationList.createInvitation(guild, invitedGuild);

        String allyInviteDoneMessage = this.messageConfig.allyInviteDone;
        allyInviteDoneMessage = StringUtils.replace(allyInviteDoneMessage, "{GUILD}", invitedGuild.getName());
        allyInviteDoneMessage = StringUtils.replace(allyInviteDoneMessage, "{TAG}", invitedGuild.getTag());
        player.sendMessage(allyInviteDoneMessage);

        if (invitedOwner != null) {
            String allyToInvitedMessage = this.messageConfig.allyToInvited;
            allyToInvitedMessage = StringUtils.replace(allyToInvitedMessage, "{GUILD}", guild.getName());
            allyToInvitedMessage = StringUtils.replace(allyToInvitedMessage, "{TAG}", guild.getTag());
            invitedOwner.sendMessage(allyToInvitedMessage);
        }
    }

}
