package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcLeader implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        Messages messages = Messages.getInstance();
        Player player = (Player) s;
        User owner = User.get(player);

        if (!owner.hasGuild()) {
            player.sendMessage(messages.getMessage("leaderHasNotGuild"));
            return;
        }

        if (!owner.isOwner()) {
            player.sendMessage(messages.getMessage("leaderIsNotOwner"));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(messages.getMessage("leaderPlayer"));
            return;
        }

        String name = args[0];
        if (!UserUtils.playedBefore(name)) {
            player.sendMessage(messages.getMessage("leaderPlayedBefore"));
            return;
        }

        User user = User.get(name);

        if (owner.equals(user)) {
            player.sendMessage(StringUtils.colored("&cNie mozesz sobie oddac zalozyciela!"));
            return;
        }

        Guild guild = owner.getGuild();

        if (!guild.getMembers().contains(user)) {
            player.sendMessage(messages.getMessage("leaderIsNotMember"));
            return;
        }

        guild.setOwner(user);
        player.sendMessage(messages.getMessage("leaderSet"));

        Player leader = Bukkit.getPlayer(user.getName());
        if (leader != null)
            leader.sendMessage(messages.getMessage("leaderOwner"));
    }
}
