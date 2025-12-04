package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@FunnyComponent
public class PanelCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.panel.name}",
            description = "${user.panel.description}",
            aliases = "${user.panel.aliases}",
            permission = "funnyguilds.panel",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player) {
        List<ItemStack> guiPanel = this.config.guiPanel;
        String titlePanel = this.config.guiPanelTitle.getValue();
    }
}
