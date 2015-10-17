package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcInvite implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messages messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.getMessage("inviteHasNotGuild"));
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.getMessage("inviteIsNotOwner"));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(messages.getMessage("invitePlayer"));
            return;
        }

        Guild guild = user.getGuild();

        if (guild.getMembers().size() >= Settings.getInstance().inviteMembers) {
            player.sendMessage(messages.getMessage("inviteAmount")
                    .replace("{AMOUNT}", Integer.toString(Settings.getInstance().inviteMembers)));
            return;
        }

        if (!UserUtils.playedBefore(args[0])) {
            player.sendMessage(StringUtils.colored("&cTen gracz nie byl nigdy na serwerze!"));
            return;
        }
        // TODO
        //OfflinePlayer invitedOffline = Bukkit.getOfflinePlayer(args[0]);
        //User invitedUser = User.get(args[0]);
        /*
		if(InvitationsList.get(invitedUser, 0).contains(guild.getTag())){
			InvitationsList.get(invitedUser, 0).remove(guild.getTag());
			player.sendMessage(messages.getMessage("inviteCancelled"));
			if(invitedOffline == null || !invitedOffline.isOnline()){
				Player inp = invitedOffline.getPlayer();
				inp.sendMessage(messages.getMessage("inviteCancelledToInvited")
					.replace("{OWNER}", player.getName())
					.replace("{GUILD}", guild.getName())
					.replace("{TAG}", guild.getTag()));
			}
			return;
		}
		
		if(invitedOffline == null || !invitedOffline.isOnline()){
			player.sendMessage(messages.getMessage("invitePlayerExists"));
			return;
		}
		
		Player invited = invitedOffline.getPlayer();
		
		if(invitedUser.hasGuild()){
			player.sendMessage(messages.getMessage("inviteHasGuild"));
			return;
		}

		InvitationsList.get(invitedUser, 0).add(guild.getTag());
		
		player.sendMessage(messages.getMessage("inviteToOwner")
			.replace("{PLAYER}", invited.getName()));
		
		invited.sendMessage(messages.getMessage("inviteToInvited")
			.replace("{OWNER}", player.getName())
			.replace("{GUILD}", guild.getName())
			.replace("{TAG}", guild.getTag()));
//		*/
    }
}
