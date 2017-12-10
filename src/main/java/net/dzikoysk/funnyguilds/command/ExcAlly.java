package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
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
            String guildNames = StringUtils.toString(InvitationList.getInvitationGuildNames(guild), false);

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

        Guild invitedGuild = GuildUtils.byTag(tag);
        if (guild.equals(invitedGuild)) {
            player.sendMessage(messages.allySame);
            return;
        }

        if (guild.getAllies().contains(invitedGuild)) {
            player.sendMessage(messages.allyAlly);
            return;
        }

        if (InvitationList.hasInvitationFrom(guild, invitedGuild)) {
            InvitationList.expireInvitation(invitedGuild, guild);

            guild.addAlly(invitedGuild);
            invitedGuild.addAlly(guild);
            player.sendMessage(StringUtils.replace(messages.allyDone, "{GUILD}", invitedGuild.getName()));

            Player owner = invitedGuild.getOwner().getPlayer();
            if (owner !=null) {
                owner.sendMessage(messages.allyIDone.replace("{GUILD}", guild.getName()));
            }

            for (User u : guild.getMembers()) {
                IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, invitedGuild);
            }

            for (User u : invitedGuild.getMembers()) {
                IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, guild);
            }

            return;
        }

        if (InvitationList.hasInvitationFrom(invitedGuild, guild)) {
            InvitationList.expireInvitation(guild, invitedGuild);
            player.sendMessage(messages.allyReturn.replace("{GUILD}", invitedGuild.getName()));

            Player owner = invitedGuild.getOwner().getPlayer();
            if (owner !=null) {
                owner.sendMessage(messages.allyIReturn.replace("{GUILD}", guild.getName()));
            }

            return;
        }

        InvitationList.createInvitation(guild, invitedGuild);
        player.sendMessage(messages.allyInviteDone.replace("{GUILD}", invitedGuild.getName()));

        Player owner = invitedGuild.getOwner().getPlayer();
        if (owner !=null) {
            owner.sendMessage(messages.allyToInvited.replace("{GUILD}", guild.getName()));
        }
    }

}
