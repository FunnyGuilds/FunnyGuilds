package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.util.InvitationsList;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ExcAlly implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messages messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.getMessage("allyHasNotGuild"));
            return;
        }

        if (!user.isOwner()) {
            player.sendMessage(messages.getMessage("allyIsNotOwner"));
            return;
        }

        Guild guild = user.getGuild();

        if (args.length < 1) {
            if (InvitationsList.get(guild, 1).getLS().isEmpty()) {
                player.sendMessage(messages.getMessage("allyHasNotInvitation"));
                return;
            }

            List<String> list = messages.getList("allyInvitationList");
            String[] msgs = list.toArray(new String[list.size()]);
            String iss = StringUtils.toString(InvitationsList.get(guild, 1).getLS(), true);

            for (String msg : msgs) {
                player.sendMessage(msg.replace("{GUILDS}", iss));
            }

            return;
        }

        String tag = args[0];

        if (!GuildUtils.tagExists(tag)) {
            player.sendMessage(StringUtils
                    .replace(messages.getMessage("allyGuildExists"), "{TAG}", tag));
            return;
        }

        Guild invitedGuild = GuildUtils.byTag(tag);

        if (guild.equals(invitedGuild)) {
            player.sendMessage(messages.getMessage("allySame"));
            return;
        }

        if (guild.getAllies().contains(invitedGuild)) {
            player.sendMessage(messages.getMessage("allyAlly"));
            return;
        }

        if (InvitationsList.get(guild, 1).contains(invitedGuild.getName())) {
            InvitationsList.get(guild, 1).remove(invitedGuild.getName());

            guild.addAlly(invitedGuild);
            invitedGuild.addAlly(guild);

            player.sendMessage(StringUtils
                    .replace(messages.getMessage("allyDone"), "{GUILD}", invitedGuild.getName())
            );

            OfflineUser of = invitedGuild.getOwner().getOfflineUser();

            if (of.isOnline()) {
                of.getPlayer().sendMessage(messages.getMessage("allyIDone")
                        .replace("{GUILD}", guild.getName())
                );
            }

            for (User u : guild.getMembers()) {
                IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, invitedGuild);
            }

            for (User u : invitedGuild.getMembers()) {
                IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, guild);
            }

            return;
        }

        if (InvitationsList.get(invitedGuild, 1).getLS().contains(guild.getName())) {
            InvitationsList.get(invitedGuild, 1).remove(guild.getName());
            player.sendMessage(messages.getMessage("allyReturn")
                    .replace("{GUILD}", invitedGuild.getName())
            );

            OfflineUser of = invitedGuild.getOwner().getOfflineUser();
            if (of.isOnline()) {
                of.getPlayer().sendMessage(messages.getMessage("allyIReturn")
                        .replace("{GUILD}", guild.getName())
                );
            }

            return;
        }

        InvitationsList.get(invitedGuild, 1).add(guild.getName());

        player.sendMessage(messages.getMessage("allyInviteDone")
                .replace("{GUILD}", invitedGuild.getName())
        );

        OfflineUser of = invitedGuild.getOwner().getOfflineUser();

        if (of.isOnline()) {
            of.getPlayer().sendMessage(messages.getMessage("allyToInvited")
                    .replace("{GUILD}", guild.getName())
            );
        }
    }

}
