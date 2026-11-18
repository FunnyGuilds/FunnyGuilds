package net.dzikoysk.funnyguilds.feature.panel;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.bukkit.entity.Player;

@FunnyComponent
public final class PanelCommand {

    private static final String CONFIGURATION_PREFIX = "${extension." + PanelModule.COMMAND_ID;

    private final PanelModule panel;

    PanelCommand(PanelModule panel) {
        this.panel = panel;
    }

    @FunnyCommand(
            name = CONFIGURATION_PREFIX + ".name}",
            aliases = CONFIGURATION_PREFIX + ".aliases}",
            description = CONFIGURATION_PREFIX + ".description}",
            permission = "funnyguilds.panel",
            playerOnly = true
    )
    public void execute(Player player) {
        this.panel.open(player);
    }
}
