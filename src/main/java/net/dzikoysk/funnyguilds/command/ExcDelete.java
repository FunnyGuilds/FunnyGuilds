package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcDelete implements Executor {

    private static final ExcConfirm confirmExecutor = new ExcConfirm();
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!user.isOwner()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

        if (config.guildDeleteCancelIfSomeoneIsOnRegion && user.getGuild().isSomeoneInRegion()) {
            player.sendMessage(messages.deleteSomeoneIsNear);
            return;
        }

        ConfirmationList.add(user.getUUID());
        
        if (config.commands.confirm.enabled) {
            player.sendMessage(messages.deleteConfirm);
        } else {
            confirmExecutor.execute(sender, null);
        }
    }

}
