package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcJoin implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messages messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (user.hasGuild()) {
            player.sendMessage(messages.getMessage("joinHasGuild"));
            return;
        }
        /*	TODO
		if(InvitationsList.get(user, 0).getLS().isEmpty()){
			player.sendMessage(messages.getMessage("joinHasNotInvitation"));
			return;
		}
		
		if(args.length < 1){
			List<String> list = messages.getList("joinInvitationList");
			String[] msgs = list.toArray(new String[list.size()]);
			String iss = StringUtils.toString(InvitationsList.get(user, 0).getLS(), true);
			for(int i = 0; i < msgs.length; i++)
				player.sendMessage(msgs[i]
					.replace("{GUILDS}", iss));
			return;
		}
		
		String tag = args[0];
		
		if(!GuildUtils.tagExists(tag)){
			player.sendMessage(messages.getMessage("joinTagExists"));
			return;
		}
		
		if(!InvitationsList.get(user, 0).contains(tag)){
			player.sendMessage(messages.getMessage("joinHasNotInvitationTo"));
			return;
		}
		
		List<ItemStack> itemsList = Settings.getInstance().joinItems;
		ItemStack[] items = itemsList.toArray(new ItemStack[0]); 
		for(int i = 0; i < items.length; i++){
			if(!player.getInventory().containsAtLeast(items[i], items[i].getAmount())){
				String msg = messages.getMessage("joinItems");
				if(msg.contains("{ITEM}")){
					StringBuilder sb = new StringBuilder();
					sb.append(items[i].getAmount());
					sb.append(" ");
					sb.append(items[i].getType().toString().toLowerCase());
					msg = msg.replace("{ITEM}", sb.toString());
				}
				if(msg.contains("{ITEMS}")){
					ArrayList<String> list = new ArrayList<String>();
					for(ItemStack it : itemsList){
						StringBuilder sb = new StringBuilder();
						sb.append(it.getAmount());
						sb.append(" ");
						sb.append(it.getType().toString().toLowerCase());
						list.add(sb.toString());
					}
					msg = msg.replace("{ITEMS}", StringUtils.toString(list, true));
				}
				player.sendMessage(msg);
				return;
			}
		}
		player.getInventory().removeItem(items);
		
		Guild guild = GuildUtils.byTag(tag);
		
		InvitationsList.get(user, 0).remove(guild.getTag());
		InvitationsList.get(user, 0).getLS().clear();
		
		guild.addMember(user);
		user.setGuild(guild);
		
		IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, user.getOfflineUser());
				
		player.sendMessage(messages.getMessage("joinToMember")
			.replace("{GUILD}", guild.getName())
			.replace("{TAG}", guild.getTag()));
		
		Player owner = Bukkit.getPlayer(guild.getOwner().getName());
		
		if(owner != null) owner.sendMessage(messages.getMessage("joinToOwner")
			.replace("{PLAYER}", player.getName()));

		Bukkit.broadcastMessage(messages.getMessage("broadcastJoin")
			.replace("{PLAYER}", player.getName())
			.replace("{GUILD}", guild.getName())
			.replace("{TAG}", tag));
		*/
    }
}
