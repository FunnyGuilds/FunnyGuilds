package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.util.InvitationsList;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcInvite extends Exc {
	
	public ExcInvite(String command, String perm){
		super(command, perm);
		this.register();
	}
		
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args){
		if (!cmd.getName().equalsIgnoreCase(Config.getInstance().excInvite)) return false;
		
		if(!(s instanceof Player)){
			FunnyGuilds.info("Console can not use this command");
			return true;
		}
		
		Messages m = Messages.getInstance();
		
		Player p = (Player) s;
		User lp = User.get(p);
		
		if(!lp.hasGuild()){
			p.sendMessage(m.getMessage("inviteHasNotGuild"));
			return true;
		}
		
		if(!lp.isOwner()){
			p.sendMessage(m.getMessage("inviteIsNotOwner"));
			return true;
		}
		
		if(args.length < 1){
			p.sendMessage(m.getMessage("invitePlayer"));
			return true;
		}
		Guild guild = lp.getGuild();
		
		if(guild.getMembers().size() >= Config.getInstance().inviteMembers){
			p.sendMessage(m.getMessage("inviteAmount")
				.replace("{AMOUNT}", Integer.toString(Config.getInstance().inviteMembers)));
			return true;
		}
			
		OfflinePlayer oi = Bukkit.getOfflinePlayer(args[0]);
		User ip = User.get(args[0]);
		
		if(InvitationsList.get(ip, 0).contains(guild.getTag())){
			InvitationsList.get(ip, 0).remove(guild.getTag());
			p.sendMessage(m.getMessage("inviteCancelled"));
			if(oi == null || !oi.isOnline()){
				Player inp = oi.getPlayer();
				inp.sendMessage(
					m.getMessage("inviteCancelledToInvited")
					.replace("{OWNER}", p.getName())
					.replace("{GUILD}", guild.getName())
					.replace("{TAG}", guild.getTag())
				);
			}
			return true;
		}
		
		if(oi == null || !oi.isOnline()){
			p.sendMessage(m.getMessage("invitePlayerExists"));
			return true;
		}
		
		Player invited = oi.getPlayer();
		
		if(ip.hasGuild()){
			p.sendMessage(m.getMessage("inviteHasGuild"));
			return true;
		}

		InvitationsList.get(ip, 0).add(guild.getTag());
		
		p.sendMessage(
			m.getMessage("inviteToOwner")
			.replace("{PLAYER}", invited.getName())
		);
		
		invited.sendMessage(
			m.getMessage("inviteToInvited")
			.replace("{OWNER}", p.getName())
			.replace("{GUILD}", guild.getName())
			.replace("{TAG}", guild.getTag())
		);
		return true;   
	}
}
