package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcConfirm implements Executor {
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		Messages m = Messages.getInstance();
		Player p = (Player) sender;
		User lp = User.get(p);

		if(!lp.hasGuild()){
			p.sendMessage(m.getMessage("deleteHasNotGuild"));
			return;
		}

		if(!lp.isOwner()){
			p.sendMessage(m.getMessage("deleteIsNotOwner"));
			return;
		}

		if(!ConfirmationList.contains(lp.getUUID())){
			p.sendMessage(m.getMessage("deleteToConfirm"));
			return;
		}

		ConfirmationList.remove(lp.getUUID());
		String name = lp.getGuild().getName();
		String tag = lp.getGuild().getTag();
		GuildUtils.deleteGuild(lp.getGuild());

		p.sendMessage(m.getMessage("deleteSuccessful")
			.replace("{GUILD}", name)
			.replace("{TAG}", tag)
		);

		Bukkit.getServer().broadcastMessage(
		m.getMessage("broadcastDelete")
			.replace("{PLAYER}", p.getName())
			.replace("{GUILD}", name)
			.replace("{TAG}", tag)
		);
	}
}
