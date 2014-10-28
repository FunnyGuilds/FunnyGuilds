package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.StringUtils;

import org.bukkit.command.CommandSender;

public class ExcGuild implements Executor {
	
	@Override
	public void execute(CommandSender s, String[] args){
		
		Messages msg = Messages.getInstance();
		
		if(args.length > 0){
			String tag = args[0];
			if(!GuildUtils.tagExists(tag)){
				s.sendMessage(msg.getMessage("infoExists"));
				return;
			}
			
			Guild guild = GuildUtils.byTag(tag);
			if(guild == null){
				s.sendMessage(msg.getMessage("infoExists"));
				return;
			}
			
			for(String m : msg.getList("infoList")){
				m = StringUtils.replace(m, "{GUILD}", guild.getName());
				m = StringUtils.replace(m, "{TAG}", guild.getTag());
				m = StringUtils.replace(m, "{OWNER}", guild.getOwner().getName());
				m = StringUtils.replace(m, "{MEMBERS}", StringUtils.toString(UserUtils.getNames(guild.getMembers()), true));
				m = StringUtils.replace(m, "{POINTS}", Integer.toString(guild.getRank().getPoints()));
				m = StringUtils.replace(m, "{KILLS}", Integer.toString(guild.getRank().getKills()));
				m = StringUtils.replace(m, "{DEATHS}", Integer.toString(guild.getRank().getDeaths()));
				m = StringUtils.replace(m, "{RANK}", Integer.toString(RankManager.getInstance().getPosition(guild)));
				if(guild.getAllies().size() > 0)
					m = StringUtils.replace(m, "{ALLIES}", StringUtils.toString(GuildUtils.getNames(guild.getAllies()), true));
				else m = StringUtils.replace(m, "{ALLIES}", "Brak");
				s.sendMessage(m);
			}
			return;
		}
		
		for(String line : msg.getList("helpList"))
			s.sendMessage(line);
	}
}
