package net.dzikoysk.funnyguilds.system.security.cheat;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.system.security.SecuritySystem;
import net.dzikoysk.funnyguilds.system.security.SecurityUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.MinecraftServerUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.PingUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class SecurityReach {

    private static final DecimalFormat FORMAT = new DecimalFormat("##.##");
    private static final double CREATIVE_REACH = 4.5;
    private static final double SURVIVAL_REACH = 3.0;
    private static final double IMPORTANCE_OF_PING = 0.93;
    private static final double IMPORTANCE_OF_TPS = 10.0;

    private SecurityReach() {}

    public static void on(Player player, double distance, SecuritySystem system) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        MessageConfiguration messages = plugin.getMessageConfiguration();
        PluginConfiguration config = plugin.getPluginConfiguration();
        double ping = PingUtils.getPing(player);
        double tpsDelayMs = (1000.0 / MinecraftServerUtils.getRecentTPS(0) - 50.0);
        double compensation = player.getGameMode().equals(GameMode.CREATIVE) ? CREATIVE_REACH : SURVIVAL_REACH;

        compensation += config.reachCompensation;
        compensation += SecurityUtils.compensationMs(IMPORTANCE_OF_PING * ping);
        compensation += SecurityUtils.compensationMs(IMPORTANCE_OF_TPS * tpsDelayMs);

        if (distance < compensation) {
            return;
        }

        String message = messages.securitySystemReach.replace("{DISTANCE}", FORMAT.format(distance));
        User user = plugin.getUserManager().getUser(player);

        system.addViolationLevel(user);
        SecurityUtils.sendToOperator(player, "Reach", message);
    }
}
