package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.IsOwner;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixUpdateGuildRequest;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildAcceptAllyInvitationEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildRevokeAllyInvitationEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildSendAllyInvitationEvent;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.text.Formatter;

import java.util.List;

public final class AllyCommand {

    @FunnyCommand(
        name = "${user.ally.name}",
        description = "${user.ally.description}",
        aliases = "${user.ally.aliases}",
        permission = "funnyguilds.ally",
        completer = "guilds:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, @IsOwner User user, String[] args) {
        Guild guild = user.getGuild();
        List<InvitationList.Invitation> invitations = InvitationList.getInvitationsFor(guild);

        if (args.length < 1) {
            if (invitations.size() == 0) {
                player.sendMessage(messages.allyHasNotInvitation);
                return;
            }

            List<String> list = messages.allyInvitationList;
            String guildNames = ChatUtils.toString(InvitationList.getInvitationGuildNames(guild), false);

            for (String msg : list) {
                player.sendMessage(msg.replace("{GUILDS}", guildNames));
            }

            return;
        }

        String tag = args[0];
        Guild invitedGuild = GuildUtils.getByTag(tag);

        if (invitedGuild == null) {
            player.sendMessage(StringUtils.replace(messages.generalGuildNotExists, "{TAG}", tag));
            return;
        }

        if (guild.equals(invitedGuild)) {
            player.sendMessage(messages.allySame);
            return;
        }

        if (guild.getAllies().contains(invitedGuild)) {
            player.sendMessage(messages.allyAlly);
            return;
        }

        if (guild.getAllies().size() >= config.maxAlliesBetweenGuilds) {
            player.sendMessage(messages.inviteAllyAmount.replace("{AMOUNT}", Integer.toString(config.maxAlliesBetweenGuilds)));
            return;
        }

        if (invitedGuild.getAllies().size() >= config.maxAlliesBetweenGuilds) {
            Formatter formatter = new Formatter()
                    .register("{GUILD}", invitedGuild.getName())
                    .register("{TAG}", invitedGuild.getTag())
                    .register("{AMOUNT}", config.maxAlliesBetweenGuilds);

            player.sendMessage(formatter.format(messages.inviteAllyTargetAmount));
            return;
        }

        if (InvitationList.hasInvitationFrom(guild, invitedGuild)) {
            if (!SimpleEventHandler.handle(new GuildAcceptAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
                return;
            }
            
            InvitationList.expireInvitation(invitedGuild, guild);

            guild.addAlly(invitedGuild);
            invitedGuild.addAlly(guild);

            String allyDoneMessage = messages.allyDone;
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{GUILD}", invitedGuild.getName());
            allyDoneMessage = StringUtils.replace(allyDoneMessage, "{TAG}", invitedGuild.getTag());
            player.sendMessage(allyDoneMessage);

            Player owner = invitedGuild.getOwner().getPlayer();

            if (owner != null) {
                String allyIDoneMessage = messages.allyIDone;
                allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{GUILD}", guild.getName());
                allyIDoneMessage = StringUtils.replace(allyIDoneMessage, "{TAG}", guild.getTag());
                owner.sendMessage(allyIDoneMessage);
            }

            ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
            ConcurrencyTaskBuilder taskBuilder = ConcurrencyTask.builder();

            for (User member : guild.getMembers()) {
                taskBuilder.delegate(new PrefixUpdateGuildRequest(member, invitedGuild));
            }

            for (User member : invitedGuild.getMembers()) {
                taskBuilder.delegate(new PrefixUpdateGuildRequest(member, guild));
            }

            ConcurrencyTask task = taskBuilder.build();
            concurrencyManager.postTask(task);
            return;
        }

        if (InvitationList.hasInvitationFrom(invitedGuild, guild)) {
            if (!SimpleEventHandler.handle(new GuildRevokeAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
                return;
            }
            
            InvitationList.expireInvitation(guild, invitedGuild);

            String allyReturnMessage = messages.allyReturn;
            allyReturnMessage = StringUtils.replace(allyReturnMessage, "{GUILD}", invitedGuild.getName());
            allyReturnMessage = StringUtils.replace(allyReturnMessage, "{TAG}", invitedGuild.getTag());
            player.sendMessage(allyReturnMessage);

            Player owner = invitedGuild.getOwner().getPlayer();

            if (owner !=null) {
                String allyIReturnMessage = messages.allyIReturn;
                allyIReturnMessage = StringUtils.replace(allyIReturnMessage, "{GUILD}", guild.getName());
                allyIReturnMessage = StringUtils.replace(allyIReturnMessage, "{TAG}", guild.getTag());
                owner.sendMessage(allyIReturnMessage);
            }

            return;
        }

        if (!SimpleEventHandler.handle(new GuildSendAllyInvitationEvent(EventCause.USER, user, guild, invitedGuild))) {
            return;
        }
        
        InvitationList.createInvitation(guild, invitedGuild);

        String allyInviteDoneMessage = messages.allyInviteDone;
        allyInviteDoneMessage = StringUtils.replace(allyInviteDoneMessage, "{GUILD}", invitedGuild.getName());
        allyInviteDoneMessage = StringUtils.replace(allyInviteDoneMessage, "{TAG}", invitedGuild.getTag());
        player.sendMessage(allyInviteDoneMessage);

        Player owner = invitedGuild.getOwner().getPlayer();

        if (owner !=null) {
            String allyToInvitedMessage = messages.allyToInvited;
            allyToInvitedMessage = StringUtils.replace(allyToInvitedMessage, "{GUILD}", guild.getName());
            allyToInvitedMessage = StringUtils.replace(allyToInvitedMessage, "{TAG}", guild.getTag());
            owner.sendMessage(allyToInvitedMessage);
        }
    }

}
