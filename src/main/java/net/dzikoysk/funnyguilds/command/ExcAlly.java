package net.dzikoysk.funnyguilds.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

public class ExcAlly implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        Player p = (Player) sender;
        User user = User.get(p);

        if (!user.hasGuild()) {
            p.sendMessage(messages.allyHasNotGuild);
            return;
        }

        if (!user.isOwner()) {
            p.sendMessage(messages.allyIsNotOwner);
            return;
        }

        Guild guild = user.getGuild();
        List<InvitationList.Invitation> invitations = InvitationList.getInvitationsFor(guild);

        if (args.length < 1) {
            if (invitations.size() == 0) {
                p.sendMessage(messages.allyHasNotInvitation);
                return;
            }

            List<String> list = messages.allyInvitationList;
            String guildNames = StringUtils.toString(InvitationList.getInvitationGuildNames(guild), false);

            for (String msg : list) {
                p.sendMessage(msg.replace("{GUILDS}", guildNames));
            }

            return;
        }

        String tag = args[0];
        if (!GuildUtils.tagExists(tag)) {
            p.sendMessage(StringUtils.replace(messages.allyGuildExists, "{TAG}", tag));
            return;
        }

        Guild invitedGuild = GuildUtils.byTag(tag);
        if (guild.equals(invitedGuild)) {
            p.sendMessage(messages.allySame);
            return;
        }

        if (guild.getAllies().contains(invitedGuild)) {
            p.sendMessage(messages.allyAlly);
            return;
        }

        if (InvitationList.hasInvitationFrom(guild, invitedGuild)) {
            InvitationList.expireInvitation(invitedGuild, guild);

            guild.addAlly(invitedGuild);
            invitedGuild.addAlly(guild);
            p.sendMessage(StringUtils.replace(messages.allyDone, "{GUILD}", invitedGuild.getName()));

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
            p.sendMessage(messages.allyReturn.replace("{GUILD}", invitedGuild.getName()));

            Player owner = invitedGuild.getOwner().getPlayer();
            if (owner !=null) {
                owner.sendMessage(messages.allyIReturn.replace("{GUILD}", guild.getName()));
            }

            return;
        }

        InvitationList.createInvitation(guild, invitedGuild);
        p.sendMessage(messages.allyInviteDone.replace("{GUILD}", invitedGuild.getName()));

        Player owner = invitedGuild.getOwner().getPlayer();
        if (owner !=null) {
            owner.sendMessage(messages.allyToInvited.replace("{GUILD}", guild.getName()));
        }
    }
}
