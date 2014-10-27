package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.StringUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcPlayer extends Exc {
	
	public ExcPlayer(String command, String perm){
		super(command, perm);
		this.register();
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args){
		if(!cmd.getName().equalsIgnoreCase(Config.getInstance().excPlayer)) return false;
		if(!(s instanceof Player)){
			FunnyGuilds.info("Console can not use this command");
			return true;
		}
	    
	    Messages msg = Messages.getInstance();
		
		Player p = (Player) s;
		String name = p.getName();
		
		if(args.length > 0) name = args[0];
		User user = User.get(name);
		
		if(user.getUUID() == null){
			s.sendMessage(msg.getMessage("playerInfoExists"));
			return true;
		}
		
		for(String m : msg.getList("playerInfoList")){
			if(user.hasGuild()){
				m = StringUtils.replace(m, "{GUILD}", user.getGuild().getName());
				m = StringUtils.replace(m, "{TAG}", user.getGuild().getTag());
			} else {
				m = StringUtils.replace(m, "{GUILD}", "Brak");
				m = StringUtils.replace(m, "{TAG}", "Brak");
			}
			m = StringUtils.replace(m, "{PLAYER}", user.getName());
			m = StringUtils.replace(m, "{POINTS}", Integer.toString(user.getRank().getPoints()));
			m = StringUtils.replace(m, "{KILLS}", Integer.toString(user.getRank().getKills()));
			m = StringUtils.replace(m, "{DEATHS}", Integer.toString(user.getRank().getDeaths()));
			m = StringUtils.replace(m, "{RANK}", Integer.toString(RankManager.getInstance().getPosition(user)));
			s.sendMessage(m);
		}
		return true;
	}

}
