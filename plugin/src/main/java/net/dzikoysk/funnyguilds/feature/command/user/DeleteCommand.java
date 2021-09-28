package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class DeleteCommand extends AbstractFunnyCommand {

    private static final ConfirmCommand CONFIRM_EXECUTOR = new ConfirmCommand();

    @FunnyCommand(
        name = "${user.delete.name}",
        description = "${user.delete.description}",
        aliases = "${user.delete.aliases}",
        permission = "funnyguilds.delete",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(Player player, @IsOwner User user, Guild guild) {
        when (this.pluginConfig.guildDeleteCancelIfSomeoneIsOnRegion && guild.isSomeoneInRegion(), this.messageConfig.deleteSomeoneIsNear);
        ConfirmationList.add(user.getUUID());

        when (this.pluginConfig.commands.confirm.enabled, this.messageConfig.deleteConfirm);
        CONFIRM_EXECUTOR.execute(player, user, guild);
    }

}
