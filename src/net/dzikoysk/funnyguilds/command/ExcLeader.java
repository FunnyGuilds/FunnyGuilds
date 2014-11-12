package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcLeader implements Executor {
	
	@Override
	public void execute(CommandSender s, String[] args){
		
		Messages m = Messages.getInstance();
		Player p = (Player) s;
		User user = User.get(p);
		
		if(!user.hasGuild()){
			p.sendMessage(m.getMessage("leaderHasNotGuild"));
			return;
		}
		
		if(!user.isOwner()){
			p.sendMessage(m.getMessage("leaderIsNotOwner"));
			return;
		}
		
		if(args.length < 1){
			p.sendMessage(m.getMessage("leaderPlayer"));
			return;
		}
		
		String name = args[0];
		if(!UserUtils.playedBefore(name)){
			p.sendMessage(m.getMessage("leaderPlayedBefore"));
			return;
		}
		
		User u = User.get(name);
		Guild guild = user.getGuild();
		
		if(!guild.getMembers().contains(u)){
			p.sendMessage(m.getMessage("leaderIsNotMember"));
			return;
		}
		
		
	}
}
