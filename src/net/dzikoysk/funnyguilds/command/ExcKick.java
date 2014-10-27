package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcKick extends Exc {
	
	public ExcKick(String command, String perm){
		super(command, perm);
		this.register();
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args){
		if(!cmd.getName().equalsIgnoreCase(Config.getInstance().excKick)) return false;
		
		if(!(s instanceof Player)){
			FunnyGuilds.info("Console can not use this command");
			return true;
		}
		
		Messages m = Messages.getInstance();
		
		Player p = (Player) s;
		User u = User.get(p);
		
		if(!u.hasGuild()){
			p.sendMessage(m.getMessage("kickHasNotGuild"));
			return true;
		}
		
		if(!u.isOwner()){
			p.sendMessage(m.getMessage("kickIsNotOwner"));
			return true;
		}
		
		if(args.length < 1){
			p.sendMessage(m.getMessage("kickPlayer"));
			return true;
		}

		OfflinePlayer up = Bukkit.getOfflinePlayer(args[0]);
		User uk = User.get(args[0]);

		if(!uk.hasGuild()){
			p.sendMessage(m.getMessage("kickToHasNotGuild"));
			return true;
		}
		
		if(!u.getGuild().equals(uk.getGuild())){
			p.sendMessage(m.getMessage("kickOtherGuild"));
			return true;
		}
		
		if(uk.isOwner()){
			p.sendMessage(m.getMessage("kickOwner"));
			return true;
		}
		
		Guild guild = u.getGuild();
		
		IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_PLAYER, up);
		
		guild.removeMember(uk);
		uk.removeGuild();
		
		if(up.isOnline()) IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE_PLAYER, p);
		
		p.sendMessage(
			m.getMessage("kickToOwner")
			.replace("{PLAYER}", uk.getName())
		);
		
		Player pk = Bukkit.getPlayer(uk.getName());
		if(pk != null) pk.sendMessage( m.getMessage("kickToPlayer")
			.replace("{GUILD}", guild.getName())
		);
		
		Bukkit.broadcastMessage(
			m.getMessage("broadcastKick")
			.replace("{PLAYER}", uk.getName())
			.replace("{GUILD}", guild.getName())
			.replace("{TAG}", guild.getTag())
		);
		return true;
	}

}
