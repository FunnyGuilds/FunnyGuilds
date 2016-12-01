package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcDelete implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        Messages m = Messages.getInstance();

        Player p = (Player) s;
        User u = User.get(p);

        if (!u.hasGuild()) {
            p.sendMessage(m.getMessage("deleteHasNotGuild"));
            return;
        }

        if (!u.isOwner()) {
            p.sendMessage(m.getMessage("deleteIsNotOwner"));
            return;
        }

        ConfirmationList.add(u.getUUID());
        p.sendMessage(m.getMessage("deleteConfirm"));
    }
}