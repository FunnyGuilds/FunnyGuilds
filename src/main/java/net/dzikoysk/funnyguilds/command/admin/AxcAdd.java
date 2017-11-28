package net.dzikoysk.funnyguilds.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;

public class AxcAdd implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();

        if (args.length < 1) {
            s.sendMessage(m.adminNoTagGiven);
            return;
        }

        if (args.length < 2) {
            s.sendMessage(m.adminNoNickGiven);
            return;
        }

        User user = User.get(args[1]);
        if (user.hasGuild()) {
            s.sendMessage(m.adminUserHasGuild);
            return;
        }

        Guild guild = GuildUtils.byTag(args[0]);
        if (guild == null) {
            s.sendMessage(m.adminNoGuildFound);
            return;
        }

        guild.addMember(user);
        user.setGuild(guild);
        IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, user.getOfflineUser());

        Player p = user.getPlayer();
        if (p !=null) {
            p.sendMessage(m.joinToMember.replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
        }

        Player owner = guild.getOwner().getPlayer();
        if (owner != null) {
            owner.sendMessage(m.joinToOwner.replace("{PLAYER}", user.getName()));
        }

        Bukkit.broadcastMessage(m.broadcastJoin.replace("{PLAYER}", user.getName()).replace("{GUILD}", guild.getName()).replace("{TAG}", guild.getTag()));
    }
}
