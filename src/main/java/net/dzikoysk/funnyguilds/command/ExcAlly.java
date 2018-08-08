package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTask;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyTaskBuilder;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixUpdateGuildRequest;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildAcceptAllyInvitationEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildRevokeAllyInvitationEvent;
import net.dzikoysk.funnyguilds.event.guild.ally.GuildSendAllyInvitationEvent;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ExcAlly implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!user.isOwner()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

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

        if (!GuildUtils.tagExists(tag)) {
            player.sendMessage(StringUtils.replace(messages.generalGuildNotExists, "{TAG}", tag));
            return;
        }

        Guild invitedGuild = GuildUtils.getByTag(tag);

        if (guild.equals(invitedGuild)) {
            player.sendMessage(messages.allySame);
            return;
        }

        if (guild.getAllies().contains(invitedGuild)) {
            player.sendMessage(messages.allyAlly);
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
