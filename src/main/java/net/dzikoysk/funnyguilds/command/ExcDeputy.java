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

public class ExcDeputy implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        Messages messages = Messages.getInstance();
        Player player = (Player) s;
        User owner = User.get(player);

        if (!owner.hasGuild()) {
            player.sendMessage(messages.getMessage("deputyHasNotGuild"));
            return;
        }

        if (!owner.isOwner()) {
            player.sendMessage(messages.getMessage("deputyIsNotOwner"));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(messages.getMessage("deputyPlayer"));
            return;
        }

        String name = args[0];
        if (!UserUtils.playedBefore(name)) {
            player.sendMessage(messages.getMessage("deputyPlayedBefore"));
            return;
        }

        User user = User.get(name);
        if (owner.equals(user)) {
            player.sendMessage(StringUtils.colored("&cNie mozesz mianowac siebie zastepca!"));
            return;
        }

        Guild guild = owner.getGuild();

        if (!guild.getMembers().contains(user)) {
            player.sendMessage(messages.getMessage("deputyIsNotMember"));
            return;
        }

        if (user.isDeputy()) {
            guild.setDeputy(null);
            player.sendMessage(messages.getMessage("deputyRemove"));
            Player o = Bukkit.getPlayer(user.getName());
            if (o != null)
                o.sendMessage(messages.getMessage("deputyMember"));
        } else {
            guild.setDeputy(user);
            player.sendMessage(messages.getMessage("deputySet"));
            Player o = Bukkit.getPlayer(user.getName());
            if (o != null)
                o.sendMessage(messages.getMessage("deputyOwner"));
        }
    }
}
