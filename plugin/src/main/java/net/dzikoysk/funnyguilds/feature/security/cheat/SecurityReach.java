package net.dzikoysk.funnyguilds.feature.security.cheat;

import java.text.DecimalFormat;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.security.SecurityUtils;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.NmsUtils;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class SecurityReach {

    private static final DecimalFormat FORMAT = new DecimalFormat("##.##");
    private static final double CREATIVE_REACH = 4.5;
    private static final double SURVIVAL_REACH = 3.0;
    private static final double IMPORTANCE_OF_PING = 0.93;
    private static final double IMPORTANCE_OF_TPS = 10.0;

    private SecurityReach() {
    }

    public static void on(Player player, double distance) {
        FunnyGuilds funnyGuilds = FunnyGuilds.getInstance();
        MessageConfiguration messages = funnyGuilds.getMessageConfiguration();
        PluginConfiguration config = funnyGuilds.getPluginConfiguration();
        UserManager userManager = funnyGuilds.getUserManager();

        double ping = NmsUtils.getPing(player);
        double tpsDelayMs = (1000.0 / NmsUtils.getTpsInLastMinute() - 50.0);
        double compensation = player.getGameMode() == GameMode.CREATIVE ? CREATIVE_REACH : SURVIVAL_REACH;

        compensation += config.reachCompensation;
        compensation += SecurityUtils.compensationMs(IMPORTANCE_OF_PING * ping);
        compensation += SecurityUtils.compensationMs(IMPORTANCE_OF_TPS * tpsDelayMs);

        if (distance < compensation) {
            return;
        }

        String message = FunnyFormatter.format(messages.securitySystemReach, "{DISTANCE}", FORMAT.format(distance));

        SecurityUtils.addViolationLevel(userManager.findByPlayer(player).orNull());
        SecurityUtils.sendToOperator(player, "Reach", message);
    }

}
