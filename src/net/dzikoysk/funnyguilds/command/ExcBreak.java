package net.dzikoysk.funnyguilds.command;

import java.util.List;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;
import net.dzikoysk.funnyguilds.util.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcBreak extends Exc {
	
	public ExcBreak(String command, String perm){
		super(command, perm);
		this.register();
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args){
		if(!cmd.getName().equalsIgnoreCase(Config.getInstance().excBreak)) return false;

		if(!(s instanceof Player)){
			FunnyGuilds.info("Console can not use this command");
			return true;
		}
	    
	    Messages m = Messages.getInstance();
		
		Player p = (Player) s;
		User lp = User.get(p);
		
	    if(!lp.hasGuild()){
	    	p.sendMessage(m.getMessage("breakHasNotGuild"));
	    	return true;
	    }
	    
	    if(!lp.isOwner()){
	    	p.sendMessage(m.getMessage("breakIsNotOwner"));
	    	return true;
	    }
	    
	    Guild guild = lp.getGuild();
	    
	    if(guild.getAllies() == null || guild.getAllies().isEmpty()){
	    	p.sendMessage(m.getMessage("breakHasNotAllies"));
	    	return true;
	    }
	    
	    if(args.length < 1){
	    	List<String> list = m.getList("breakAlliesList");
			String[] msgs =  list.toArray(new String[list.size()]);
			String iss = StringUtils.toString(GuildUtils.getNames(guild.getAllies()), true);
			for(int i = 0; i < msgs.length; i++)
				p.sendMessage(msgs[i]
					.replace("{GUILDS}", iss)
				);
			return true;
	    }
	    
	    String tag = args[0];
	    
	    if(!GuildUtils.tagExists(tag)){
	    	p.sendMessage(
	    		m.getMessage("breakGuildExists")
	    		.replace("{TAG}", tag)
	    	);
	    	return true;
		}
	    
	    Guild tb = GuildUtils.byTag(tag);
	    
	    if(!guild.getAllies().contains(tb)){
	    	p.sendMessage(
	    		m.getMessage("breakAllyExists")
	    		.replace("{GUILD}", tb.getName())
	    		.replace("{TAG}", tag)
		    );
	    }
	    
	    guild.removeAlly(tb);
	    tb.removeAlly(guild);
	    
	    for(User u : guild.getMembers())
	    	IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, tb);
		for(User u : tb.getMembers())
			IndependentThread.action(ActionType.PREFIX_UPDATE_GUILD, u, guild);
		
	    p.sendMessage(
	    	m.getMessage("breakDone")
	    	.replace("{GUILD}", tb.getName())
	    	.replace("{TAG}", tb.getTag())
	    );
	    
	    OfflinePlayer of = Bukkit.getOfflinePlayer(tb.getOwner().getName());
	    if(of.isOnline()) of.getPlayer().sendMessage(
	    	m.getMessage("allyIDone")
	    	.replace("{GUILD}", guild.getName())
	    	.replace("{TAG}", guild.getTag())
		);
	    
		return true;
	}
}
