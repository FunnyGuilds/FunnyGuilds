package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.data.Config;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.Parser;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ExcTop extends Exc {
	
	public ExcTop(String command, String perm){
		super(command, perm);
		this.register();
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args){
		if(!cmd.getName().equalsIgnoreCase(Config.getInstance().excTop)) return false;

		for(String m : Messages.getInstance().getList("topList")){
			String r = Parser.parseRank(m);
			if(r != null) m = r;
			s.sendMessage(m);
		}
		return true;
	}

}
