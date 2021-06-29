package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.IsOwner;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import org.bukkit.entity.Player;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

@FunnyComponent
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
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, @IsOwner User user, Guild guild) {
        when (config.guildDeleteCancelIfSomeoneIsOnRegion && guild.isSomeoneInRegion(), messages.deleteSomeoneIsNear);
        ConfirmationList.add(user.getUUID());

        when (config.commands.confirm.enabled, messages.deleteConfirm);
        CONFIRM_EXECUTOR.execute(config, messages, player, user, guild);
    }

}
