package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcLeave extends Exc {
	
	public ExcLeave(String command, String perm){
		super(command, perm);
		this.register();
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args){
		if(!cmd.getName().equalsIgnoreCase(Config.getInstance().excLeave)) return false;
		
		if(!(s instanceof Player)){
			FunnyGuilds.info("Console can not use this command");
			return true;
		}
		
		Messages m = Messages.getInstance();
		
		Player p = (Player) s;
		User u = User.get(p);
		
		if(!u.hasGuild()){
			p.sendMessage(m.getMessage("leaveHasNotGuild"));
			return true;
		}
		
		if(u.isOwner()){
			p.sendMessage(m.getMessage("leaveIsOwner"));
			return true;
		}
		
		Guild guild = u.getGuild();
		IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_PLAYER, p);
		guild.removeMember(u);
		u.removeGuild();
		IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE_PLAYER, p);
		
		p.sendMessage(
			m.getMessage("leaveToUser")
			.replace("{GUILD}", guild.getName())
			.replace("{TAG}", guild.getTag())
		);
		
		Bukkit.broadcastMessage(
			m.getMessage("broadcastLeave")
			.replace("{PLAYER}", u.getName())
			.replace("{GUILD}", guild.getName())
			.replace("{TAG}", guild.getTag())
		);
		return true;
	}
}
