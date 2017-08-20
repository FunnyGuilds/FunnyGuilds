package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcDelete implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) s;
        User u = User.get(p);

        if (!u.hasGuild()) {
            p.sendMessage(m.deleteHasNotGuild);
            return;
        }

        if (!u.isOwner()) {
            p.sendMessage(m.deleteIsNotOwner);
            return;
        }

        if (Settings.getConfig().regionDeleteIfNear && u.getGuild().isSomeoneInRegion()) {
            p.sendMessage(m.deleteSomeoneIsNear);
            return;
        }

        ConfirmationList.add(u.getUUID());
        p.sendMessage(m.deleteConfirm);
    }

}