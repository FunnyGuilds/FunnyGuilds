package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcLeave implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {

        MessagesConfig m = Messages.getInstance();
        Player p = (Player) s;
        User u = User.get(p);

        if (!u.hasGuild()) {
            p.sendMessage(m.leaveHasNotGuild);
            return;
        }

        if (u.isOwner()) {
            p.sendMessage(m.leaveIsOwner);
            return;
        }

        Guild guild = u.getGuild();
        IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_PLAYER, u.getOfflineUser());
        guild.removeMember(u);
        u.removeGuild();
        IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE_PLAYER, p);

        p.sendMessage(
                m.leaveToUser
                        .replace("{GUILD}", guild.getName())
                        .replace("{TAG}", guild.getTag())
        );

        Bukkit.broadcastMessage(
                m.broadcastLeave
                        .replace("{PLAYER}", u.getName())
                        .replace("{GUILD}", guild.getName())
                        .replace("{TAG}", guild.getTag())
        );
    }
}
