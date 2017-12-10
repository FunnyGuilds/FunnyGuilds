package net.dzikoysk.funnyguilds.command.manager;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MxcPvP implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

        Guild guild = user.getGuild();
        if (guild.getPvP()) {
            guild.setPvP(false);
            player.sendMessage(messages.pvpOff);
        } else {
            guild.setPvP(true);
            player.sendMessage(messages.pvpOn);
        }
    }
}
