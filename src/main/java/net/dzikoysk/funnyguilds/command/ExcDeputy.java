package net.dzikoysk.funnyguilds.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;

public class ExcDeputy implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {

        MessagesConfig m = Messages.getInstance();
        Player p = (Player) sender;
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
            p.sendMessage(m.deputyMustBeDifferent);
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
            
            Player o = user.getPlayer();
            if (o != null) {
                o.sendMessage(m.deputyMember);
            }

            return;
        }

        guild.setDeputy(user);
        p.sendMessage(m.deputySet);
        
        Player o = user.getPlayer();
        if (o != null) {
            o.sendMessage(m.deputyOwner);
        }
    }
}
