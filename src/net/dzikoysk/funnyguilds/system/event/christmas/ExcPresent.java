package net.dzikoysk.funnyguilds.system.event.christmas;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.util.LangUtils;
import net.dzikoysk.funnyguilds.util.StringUtils;

public class ExcPresent implements Executor {

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		ChristmasUser cu = ChristmasUser.get(User.get(player));
		
		player.sendMessage(StringUtils.colored("&8----------------------------------"));
		player.sendMessage(StringUtils.colored("&4Twoje dotychczasowe osiagniecia:"));
		player.sendMessage(StringUtils.colored("&2Wszystkie otwarte prezenty: &4" + cu.getGifts()));
		player.sendMessage(StringUtils.colored("&2Czerwone prezenty: &4" + cu.getRedGifts()));
		player.sendMessage(StringUtils.colored("&2Zielone prezenty: &4" + cu.getGreenGifts()));
		player.sendMessage(StringUtils.colored("&2Emeraldowe prezenty: &4" + cu.getEmeraldGifts()));
		player.sendMessage(StringUtils.colored("&2Mozliwosc darmowego zalozenia gildii: &4" + LangUtils.get(cu.hasBypass())));
		player.sendMessage(StringUtils.colored("&8----------------------------------"));
	}

}
