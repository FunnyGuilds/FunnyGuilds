package net.dzikoysk.funnyguilds.system.event.christmas;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.util.StringUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcPresents implements Executor {

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		ChristmasStats cs = ChristmasStats.getInstance();
		
		player.sendMessage(StringUtils.colored("&8----------{ &cPrezentowe Top 10 &8}----------"));
		for(int i = 1; i < 11; i++){
			ChristmasUser cu = cs.getChristmasUser(i);
			if(cu == null) player.sendMessage(StringUtils.colored(i + ". &cBrak"));
			else player.sendMessage(StringUtils.colored(i + ". &c" + cu.getName() + " &f[&c" + cu.getGifts() + "&f]"));
		}
	}

}
