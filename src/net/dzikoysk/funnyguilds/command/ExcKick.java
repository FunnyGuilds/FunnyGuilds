package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcKick implements Executor {
	
	@Override
	public void execute(CommandSender s, String[] args){
		
		Messages m = Messages.getInstance();
		
		Player p = (Player) s;
		User u = User.get(p);
		
		if(!u.hasGuild()){
			p.sendMessage(m.getMessage("kickHasNotGuild"));
			return;
		}
		
		if(!u.isOwner() && !u.isDeputy()){
			p.sendMessage(m.getMessage("kickIsNotOwner"));
			return;
		}
		
		if(args.length < 1){
			p.sendMessage(m.getMessage("kickPlayer"));
			return;
		}

		OfflinePlayer up = Bukkit.getOfflinePlayer(args[0]);
		User uk = User.get(args[0]);

		if(!uk.hasGuild()){
			p.sendMessage(m.getMessage("kickToHasNotGuild"));
			return;
		}
		
		if(!u.getGuild().equals(uk.getGuild())){
			p.sendMessage(m.getMessage("kickOtherGuild"));
			return;
		}
		
		if(uk.isOwner()){
			p.sendMessage(m.getMessage("kickOwner"));
			return;
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
	}

}
