package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcKick implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) sender;
        User u = User.get(p);

        if (!u.hasGuild()) {
            p.sendMessage(m.kickHasNotGuild);
            return;
        }

        if (!u.isOwner() && !u.isDeputy()) {
            p.sendMessage(m.kickIsNotOwner);
            return;
        }

        if (args.length < 1) {
            p.sendMessage(m.kickPlayer);
            return;
        }

        User uk = User.get(args[0]);
        OfflineUser up = uk.getOfflineUser();

        if (!uk.hasGuild()) {
            p.sendMessage(m.kickToHasNotGuild);
            return;
        }

        if (!u.getGuild().equals(uk.getGuild())) {
            p.sendMessage(m.kickOtherGuild);
            return;
        }

        if (uk.isOwner()) {
            p.sendMessage(m.kickOwner);
            return;
        }

        Guild guild = u.getGuild();
        IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_PLAYER, up);

        guild.removeMember(uk);
        uk.removeGuild();

        if (up.isOnline()) {
            IndependentThread.action(ActionType.PREFIX_GLOBAL_UPDATE_PLAYER, p);
        }

        p.sendMessage(m.kickToOwner.replace("{PLAYER}", uk.getName()));

        Player pk = uk.getPlayer();
        if (pk != null) {
            pk.sendMessage(m.kickToPlayer.replace("{GUILD}", guild.getName()));
        }

        Bukkit.broadcastMessage(m.broadcastKick.replace("{PLAYER}", uk.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }
}
