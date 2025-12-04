package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.feature.gui.permission.MemberPermissionsListGui;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

@FunnyComponent
public class PermissionsCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.permissions.name}",
            description = "${user.permissions.description}",
            aliases = "${user.permissions.aliases}",
            permission = "funnyguilds.permissions",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @HasGuildPermission(GuildCommandPermission.PANEL) User user, Guild guild) {
        // Check if permissions panel is enabled
        if (!this.config.permissionsPanel.enabled) {
            this.messageService.getMessage(config -> config.permissionsPanelDisabled)
                    .receiver(player)
                    .send();
            return;
        }

        // Only leader can manage permissions
        if (!guild.isOwner(user)) {
            this.messageService.getMessage(config -> config.permissionsPanelNotLeader)
                    .receiver(player)
                    .send();
            return;
        }

        // Check if there are other members
        if (guild.getMembers().size() <= 1) {
            this.messageService.getMessage(config -> config.permissionsPanelNoMembers)
                    .receiver(player)
                    .send();
            return;
        }

        // Open the permissions panel GUI
        new MemberPermissionsListGui(
                this.config,
                this.messageService,
                guild,
                user,
                0
        ).open(player);
    }
}
