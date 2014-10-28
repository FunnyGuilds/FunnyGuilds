package net.dzikoysk.funnyguilds.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.DataManager;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;

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

		DataManager.getInstance().stop();
		ConfirmationList.remove(lp.getUUID());

		Guild guild = lp.getGuild();
		String name = guild.getName();
		String tag = guild.getTag();

		Region region = Region.get(guild.getRegion());
		Block block = region.getCenter().getBlock().getRelative(BlockFace.DOWN);
		if(block.getLocation().getBlockY() > 1) block.setType(Material.AIR);


		IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_GUILD, guild);
		GuildUtils.deleteGuild(guild);
		DataManager.getInstance().start();

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
