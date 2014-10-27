package net.dzikoysk.funnyguilds.command;

import java.util.List;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.util.InvitationsList;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;
import net.dzikoysk.funnyguilds.util.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcJoin extends Exc {
	
	public ExcJoin(String command, String perm){
		super(command, perm);
		this.register();
	}

	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args){
		if (!cmd.getName().equalsIgnoreCase(Config.getInstance().excJoin)) return false;
		
		if(!(s instanceof Player)){
			FunnyGuilds.info("Console can not use this command");
			return true;
		}
		
		Messages m = Messages.getInstance();
		
		Player p = (Player) s;
		User lp = User.get(p);
		
		if(lp.hasGuild()){
			p.sendMessage(m.getMessage("joinHasGuild"));
			return true;
		}
				
		if(InvitationsList.get(lp, 0).getLS().isEmpty()){
			p.sendMessage(m.getMessage("joinHasNotInvitation"));
			return true;
		}
		
		if(args.length < 1){
			List<String> list = m.getList("joinInvitationList");
			String[] msgs = list.toArray(new String[list.size()]);
			String iss = StringUtils.toString(InvitationsList.get(lp, 0).getLS(), true);
			for(int i = 0; i < msgs.length; i++)
				p.sendMessage(msgs[i]
					.replace("{GUILDS}", iss)
				);
			return true;
		}
		
		String tag = args[0];
		if(!GuildUtils.tagExists(tag)){
			p.sendMessage(m.getMessage("joinTagExists"));
			return true;
		}
		
		if(!InvitationsList.get(lp, 0).contains(tag)){
			p.sendMessage(m.getMessage("joinHasNotInvitationTo"));
			return true;
		}
		
		Guild guild = GuildUtils.byTag(tag);
		
		InvitationsList.get(lp, 0).remove(guild.getTag());
		InvitationsList.get(lp, 0).getLS().clear();
		
		guild.addMember(lp);
		lp.setGuild(guild);
		
	    IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, p);
				
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
	
		return true;
	}
}
