package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.util.ConfirmationList;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsOwner;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class DeleteCommand extends AbstractFunnyCommand {

    private final ConfirmCommand confirmExecutor;

    public DeleteCommand(FunnyGuilds plugin) throws Throwable {
        this.confirmExecutor = plugin.getInjector().newInstanceWithFields(ConfirmCommand.class);
    }

    @FunnyCommand(
            name = "${user.delete.name}",
            description = "${user.delete.description}",
            aliases = "${user.delete.aliases}",
            permission = "funnyguilds.delete",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @IsOwner User user, Guild guild) {
        when(config.guildDeleteCancelIfSomeoneIsOnRegion && regionManager.isAnyUserInRegion(guild.getRegion().getOrNull(), guild.getMembers()), messages.deleteSomeoneIsNear);
        ConfirmationList.add(user.getUUID());

        when(config.commands.confirm.enabled, messages.deleteConfirm);
        confirmExecutor.execute(player, user, guild);
    }

}
