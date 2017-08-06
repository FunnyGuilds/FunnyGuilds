package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcDeputy implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {

        MessagesConfig m = Messages.getInstance();
        Player p = (Player) s;
        User owner = User.get(p);

        if (!owner.hasGuild()) {
            p.sendMessage(m.deputyHasNotGuild);
            return;
        }

        if (!owner.isOwner()) {
            p.sendMessage(m.deputyIsNotOwner);
            return;
        }

        if (args.length < 1) {
            p.sendMessage(m.deputyPlayer);
            return;
        }

        String name = args[0];
        if (!UserUtils.playedBefore(name)) {
            p.sendMessage(m.deputyPlayedBefore);
            return;
        }

        User user = User.get(name);
        if (owner.equals(user)) {
            p.sendMessage(StringUtils.colored("&cNie mozesz mianowac siebie zastepca!"));
            return;
        }

        Guild guild = owner.getGuild();

        if (!guild.getMembers().contains(user)) {
            p.sendMessage(m.deputyIsNotMember);
            return;
        }

        if (user.isDeputy()) {
            guild.setDeputy(null);
            p.sendMessage(m.deputyRemove);
            Player o = Bukkit.getPlayer(user.getName());
            if (o != null) {
                o.sendMessage(m.deputyMember);
            }
        } else {
            guild.setDeputy(user);
            p.sendMessage(m.deputySet);
            Player o = Bukkit.getPlayer(user.getName());
            if (o != null) {
                o.sendMessage(m.deputyOwner);
            }
        }
    }
}
