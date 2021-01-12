package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import org.bukkit.entity.Player;

public final class DeleteCommand {

    private static final ConfirmCommand CONFIRM_EXECUTOR = new ConfirmCommand();

    @FunnyCommand(
        name = "${user.delete.name}",
        description = "${user.delete.description}",
        aliases = "${user.delete.aliases}",
        permission = "funnyguilds.delete",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, User user) {
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
            return;
        }

        CONFIRM_EXECUTOR.execute(config, messages, player, user);
    }

}
