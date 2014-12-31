package net.dzikoysk.funnyguilds.system.event.new_year;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.util.StringUtils;

public class ExcFireworks implements Executor {

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		player.sendMessage(StringUtils.colored("&bFajerwerki!"));
		new NewYearFireworks().spawn();
	}

}
