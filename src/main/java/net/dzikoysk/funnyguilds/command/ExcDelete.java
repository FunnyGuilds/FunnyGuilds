package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcDelete implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messages messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.getMessage("deleteHasNotGuild"));
            return;
        }

        if (!user.isOwner()) {
            player.sendMessage(messages.getMessage("deleteIsNotOwner"));
            return;
        }

        //ConfirmationList.add(user.getUUID());
        player.sendMessage(messages.getMessage("deleteConfirm"));
    }
}
