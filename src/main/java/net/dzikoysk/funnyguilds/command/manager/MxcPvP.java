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
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) sender;
        User user = User.get(p);

        if (!user.hasGuild()) {
            p.sendMessage(m.pvpHasNotGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            p.sendMessage(m.pvpIsNotOwner);
            return;
        }

        Guild guild = user.getGuild();
        if (guild.getPvP()) {
            guild.setPvP(false);
            p.sendMessage(m.pvpOff);
        } else {
            guild.setPvP(true);
            p.sendMessage(m.pvpOn);
        }
    }
}
