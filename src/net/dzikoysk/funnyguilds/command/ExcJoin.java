package net.dzikoysk.funnyguilds.command;

import java.util.ArrayList;
import java.util.List;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.util.InvitationsList;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExcJoin implements Executor {
	
	@Override
	public void execute(CommandSender s, String[] args){
		Messages m = Messages.getInstance();
		Player p = (Player) s;
		User user = User.get(p);
		
		if(user.hasGuild()){
			p.sendMessage(m.getMessage("joinHasGuild"));
			return;
		}
				
		if(InvitationsList.get(user, 0).getLS().isEmpty()){
			p.sendMessage(m.getMessage("joinHasNotInvitation"));
			return;
		}
		
		if(args.length < 1){
			List<String> list = m.getList("joinInvitationList");
			String[] msgs = list.toArray(new String[list.size()]);
			String iss = StringUtils.toString(InvitationsList.get(user, 0).getLS(), true);
			for(int i = 0; i < msgs.length; i++)
				p.sendMessage(msgs[i]
					.replace("{GUILDS}", iss)
				);
			return;
		}
		
		String tag = args[0];
		if(!GuildUtils.tagExists(tag)){
			p.sendMessage(m.getMessage("joinTagExists"));
			return;
		}
		
		if(!InvitationsList.get(user, 0).contains(tag)){
			p.sendMessage(m.getMessage("joinHasNotInvitationTo"));
			return;
		}
		
		List<ItemStack> itemsList = Settings.getInstance().joinItems;
		ItemStack[] items = itemsList.toArray(new ItemStack[0]); 
		for(int i = 0; i < items.length; i++){
			if(!p.getInventory().containsAtLeast(items[i], items[i].getAmount())){
				String msg = m.getMessage("joinItems");
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
				p.sendMessage(msg);
				return;
			}
		}
		p.getInventory().removeItem(items);
		
		Guild guild = GuildUtils.byTag(tag);
		
		InvitationsList.get(user, 0).remove(guild.getTag());
		InvitationsList.get(user, 0).getLS().clear();
		
		guild.addMember(user);
		user.setGuild(guild);
		
	    IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, user.getOfflineUser());
				
		p.sendMessage(m.getMessage("joinToMember")
			.replace("{GUILD}", guild.getName())
			.replace("{TAG}", guild.getTag())
		);
		
		Player owner = Bukkit.getPlayer(guild.getOwner().getName());
		if(owner != null) owner.sendMessage(m.getMessage("joinToOwner")
			.replace("{PLAYER}", p.getName())
		);

		Bukkit.broadcastMessage(m.getMessage("broadcastJoin")
			.replace("{PLAYER}", p.getName())
			.replace("{GUILD}", guild.getName())
			.replace("{TAG}", tag)
		);
	}
}
