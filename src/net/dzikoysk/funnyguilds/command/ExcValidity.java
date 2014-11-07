package net.dzikoysk.funnyguilds.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.StringUtils;

public class ExcValidity implements Executor {

	@Override
	public void execute(CommandSender sender, String[] args) {
		Messages m = Messages.getInstance();
		Player p = (Player)sender;
		User user = User.get(p);
		
		if(!user.hasGuild()){
			p.sendMessage(m.getMessage("validityHasNotGuild"));
			return;
		}
		
		if(!user.isOwner()){
			p.sendMessage(m.getMessage("validityIsNotOwner"));
			return;
		}
	
		List<ItemStack> itemsList = Config.getInstance().validityItems;
		ItemStack[] items = itemsList.toArray(new ItemStack[0]); 
		for(int i = 0; i < items.length; i++){
			if(!p.getInventory().containsAtLeast(items[i], items[i].getAmount())){
				String msg = m.getMessage("validityItems");
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
		
		Guild guild = user.getGuild();
		long c = guild.getValidity();
		if(c == 0) c = System.currentTimeMillis();
		c += Config.getInstance().validityTime;
		guild.setValidity(c);
		
		DateFormat date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Date v = new Date(c);
		
		p.sendMessage(m.getMessage("validityDone")
			.replace("{DATE}", date.format(v))
		);
	}

}
