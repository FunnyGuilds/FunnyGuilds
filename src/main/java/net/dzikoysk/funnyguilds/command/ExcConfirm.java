package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.event.EventCaller;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcConfirm implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messages messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.getMessage("deleteHasNotGuild"));
            return;
        }

        if (!user.isOwner()) {
            player.sendMessage(messages.getMessage("deleteIsNotOwner"));
            return;
        }

        Guild guild = user.getGuild();
        String name = guild.getName();
        String tag = guild.getTag();

		/*
		if(!ConfirmationList.contains(lp.getUUID())){
			p.sendMessage(m.getMessage("deleteToConfirm"));
			return;
		}

		ConfirmationList.remove(lp.getUUID());
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
		*/

        EventCaller.callEvent(new GuildDeleteEvent(player, name, tag));
    }
}
