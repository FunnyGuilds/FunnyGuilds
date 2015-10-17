package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcAlly implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        Messages messages = Messages.getInstance();
        Player player = (Player) s;
        User user = User.get(player);
        //Guild guild = user.getGuild();

        if (!user.hasGuild()) {
            player.sendMessage(messages.getMessage("allyHasNotGuild"));
            return;
        }

        if (!user.isOwner()) {
            player.sendMessage(messages.getMessage("allyIsNotOwner"));
            return;
        }
        /*
		if(args.length < 1){
			if(InvitationsList.get(guild, 1).getLS().isEmpty()){
				player.sendMessage(messages.getMessage("allyHasNotInvitation"));
				return;
			}
			
			List<String> list = messages.getList("allyInvitationList");
			String[] msgs =  list.toArray(new String[list.size()]);
			String iss = StringUtils.toString(InvitationsList.get(guild, 1).getLS(), true);
			for(int i = 0; i < msgs.length; i++)
				player.sendMessage(msgs[i]
					.replace("{GUILDS}", iss));
			return;
		}
		
		String tag = args[0];
		
		if(!GuildUtils.tagExists(tag)){
			player.sendMessage(StringUtils
				.replace(messages.getMessage("allyGuildExists"), "{TAG}", tag));
			return;
		}
		
		Guild invited = GuildUtils.byTag(tag);
		
		if(guild.equals(invited)){
			player.sendMessage(messages.getMessage("allySame"));
			return;
		}
		
		if(guild.getAllies().contains(invited)){
			player.sendMessage(messages.getMessage("allyAlly"));
			return;
		}
		
		if(InvitationsList.get(guild, 1).contains(invited.getName())){
			InvitationsList.get(guild, 1).remove(invited.getName());
			
			guild.addAlly(invited);
			invited.addAlly(guild);
			
			player.sendMessage(StringUtils
				.replace(messages.getMessage("allyDone"), "{GUILD}", invited.getName()));
			
			OfflineUser of = invited.getOwner().getOfflineUser();
			if(of.isOnline()) of.getPlayer().sendMessage(messages.getMessage("allyIDone")
				.replace("{GUILD}", guild.getName()));
			
			for(User u : guild.getMembers())
				IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, invited);
			for(User u : invited.getMembers())
				IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, guild);

			return;
		}
		
		if(InvitationsList.get(invited, 1).getLS().contains(guild.getName())){
			InvitationsList.get(invited, 1).remove(guild.getName());
			player.sendMessage(
				messages.getMessage("allyReturn")
				.replace("{GUILD}", invited.getName()));
			
			OfflineUser of = invited.getOwner().getOfflineUser();
			if(of.isOnline()) of.getPlayer().sendMessage(messages.getMessage("allyIReturn")
				.replace("{GUILD}", guild.getName()));
			
			return;
		}
		
		InvitationsList.get(invited, 1).add(guild.getName());
		
		player.sendMessage(messages.getMessage("allyInviteDone")
			.replace("{GUILD}", invited.getName()));
		
		OfflineUser offlineInvitedOwner = invited.getOwner().getOfflineUser();
		if(offlineInvitedOwner.isOnline()) offlineInvitedOwner.getPlayer().sendMessage(messages.getMessage("allyToInvited")
			.replace("{GUILD}", guild.getName()));
		return;
		*/
    }
}
