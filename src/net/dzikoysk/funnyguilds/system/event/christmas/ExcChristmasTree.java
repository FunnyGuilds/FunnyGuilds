package net.dzikoysk.funnyguilds.system.event.christmas;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.util.StringUtils;

public class ExcChristmasTree implements Executor {

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		
		int all 	= 0;
		int red 	= 0;
		int green 	= 0;
		int emerald = 0;
		
		for(ChristmasUser user : ChristmasUser.getUsers()){
			all 	+= user.getGifts();
			red 	+= user.getRedGifts();
			green 	+= user.getGreenGifts();
			emerald += user.getEmeraldGifts();
		}
		
		player.sendMessage(StringUtils.colored("&8-----------------------------------------"));
		player.sendMessage(StringUtils.colored("&4Oto co zostalo zebrane pod serwerowa choinka:"));
		player.sendMessage(StringUtils.colored("&2Wszystkie otwarte prezenty: &4" + all));
		player.sendMessage(StringUtils.colored("&2Czerwone prezenty: &4" + red));
		player.sendMessage(StringUtils.colored("&2Zielone prezenty: &4" + green));
		player.sendMessage(StringUtils.colored("&2Emeraldowe prezenty: &4" + emerald));
		player.sendMessage(StringUtils.colored("&8-----------------------------------------"));
	}

}
