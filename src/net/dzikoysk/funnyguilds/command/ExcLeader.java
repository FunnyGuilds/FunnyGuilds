package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcLeader implements Executor {
	
	@Override
	public void execute(CommandSender s, String[] args){
		Messages m = Messages.getInstance();
		Player p = (Player) s;
		User owner = User.get(p);
		
		if(!owner.hasGuild()){
			p.sendMessage(m.getMessage("leaderHasNotGuild"));
			return;
		}
		
		if(!owner.isOwner()){
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
		
		User user = User.get(name);
		if(owner.equals(user)){
			p.sendMessage(StringUtils.colored("&cNie mozesz sobie oddac zalozyciela!"));
			return;
		}
		
		Guild guild = owner.getGuild();
		
		if(!guild.getMembers().contains(user)){
			p.sendMessage(m.getMessage("leaderIsNotMember"));
			return;
		}
		
		guild.setOwner(user);
		
		p.sendMessage(m.getMessage("leaderSet"));
		
		Player o = Bukkit.getPlayer(user.getName());
		if(o != null) o.sendMessage(m.getMessage("leaderOwner"));
	}
}
